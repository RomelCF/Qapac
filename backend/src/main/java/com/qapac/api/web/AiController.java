package com.qapac.api.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.URI;

@RestController
@RequestMapping("/ai")
public class AiController {

    @Value("${gemini.api.key:${GEMINI_API_KEY:}}")
    private String geminiApiKey;

    @Value("${gemini.model:${GEMINI_MODEL:models/gemini-pro}}")
    private String geminiModel;

    public static record ChatRequest(String message) {}
    public static record ChatResponse(String reply) {}

    private String buildGeminiUrl() {
        // v1 con prefijo models/ y modelo parametrizado (p.ej. gemini-2.5-flash, gemini-pro)
        return "https://generativelanguage.googleapis.com/v1/models/" + geminiModel + ":generateContent?key=" + geminiApiKey;
    }

    @PostMapping("/chat")
    public ResponseEntity<?> chat(@RequestBody ChatRequest req) {
        if (req == null || req.message() == null || req.message().isBlank()) {
            return ResponseEntity.badRequest().body("Mensaje vacío");
        }
        if (geminiApiKey == null || geminiApiKey.isBlank()) {
            return ResponseEntity.status(500).body("Falta configurar gemini.api.key o GEMINI_API_KEY");
        }
        try {
            String systemContext = String.join("\n",
                "Eres un asistente de soporte para la aplicación Qapac.",
                "Responde SOLO sobre funcionalidades, pantallas, flujos y uso de Qapac (no temas generales).",
                "Si te preguntan algo fuera del alcance de Qapac, responde brevemente que solo puedes ayudar con el sistema.",
                "Guía práctica (responde con pasos concretos y breves):",
                "- Cliente:",
                "  · Dashboard: acceso a Mis pasajes, Catálogo (comprar), Movimientos, Tarjetas, Carrito, Mi cuenta.",
                "  · Comprar: usa /compras/opciones; filtra por origen/destino/fecha; botón ‘Añadir al carrito’ → selector de asientos → confirmar.",
                "  · Carrito/Pago: seleccionar método (tarjeta o similar), confirmar; genera boleta y muestra total/neto.",
                "  · Mis pasajes: ver detalle; cancelar si aplica (muestra reembolso estimado); estados finalizados resaltan.",
                "  · Movimientos: lista compras/cancelaciones con filtros; KPIs de totales.",
                "  · Tarjetas: listar/añadir/eliminar; soporta Visa/Mastercard.",
                "  · Mi cuenta: cambiar avatar/logo, correo (demo), contraseña (demo). Cabecera usa avatar en la esquina superior derecha.",
                "- Empresa:",
                "  · Buses: crear/editar; capacidad; gestión de asientos (códigos tipo A1, A2…); generación automática; no exceder capacidad.",
                "  · Viajes/Rutas: asignar bus y fechas/horas; estadística por ocupación.",
                "  · Ventas: rango por días o por mes; detalle por venta; exporte/imprima reporte.",
                "  · Estadísticas: KPIs (ingresos, tickets, empresas/buses activos), series diarias, rutas top; tolerante a datos faltantes.",
                "- Administrador:",
                "  · Usuarios: CRUD; alternar admin.",
                "  · Empleados: CRUD; chofer/azafato; brevete (crear/editar/quitar).",
                "  · Sucursales: CRUD.",
                "  · Estadísticas admin: KPIs globales, ventas diarias, ventas por empresa, rutas top.",
                "- UI/Patrones: botón flotante de chat (abajo derecha); cabeceras coherentes; modo oscuro; toasts de confirmación.",
                "Responde con lenguaje claro, usando los nombres de botones y secciones tal como aparecen en la app."
            );

            String payload = "{\n" +
                    "  \"contents\": [\n" +
                    "    {\n" +
                    "      \"role\": \"user\",\n" +
                    "      \"parts\": [ { \"text\": " + jsonString(systemContext + "\n\n" + req.message()) + " } ]\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(buildGeminiUrl()))
                    .header("Content-Type", "application/json")
                    .POST(BodyPublishers.ofString(payload))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> resp = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
                String reply = extractText(resp.body());
                if (reply == null || reply.isBlank()) {
                    reply = "No pude generar respuesta en este momento.";
                }
                return ResponseEntity.ok(new ChatResponse(reply));
            }
            return ResponseEntity.status(resp.statusCode()).body("Error de IA: " + resp.body());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error llamando a IA");
        }
    }

    private static String jsonString(String s) {
        String escaped = s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
        return "\"" + escaped + "\"";
    }

    private static String extractText(String body) {
        // Extrae y concatena todas las ocurrencias de parts[].text del primer candidate
        String key = "\"text\":"; // tolera espacios entre : y el valor
        StringBuilder result = new StringBuilder();
        int from = 0;
        boolean foundAny = false;
        while (true) {
            int i = body.indexOf(key, from);
            if (i < 0) break;
            int start = i + key.length();
            // saltar espacios en blanco
            while (start < body.length() && Character.isWhitespace(body.charAt(start))) start++;
            if (start >= body.length() || body.charAt(start) != '"') { from = start + 1; continue; }
            start++; // posicionarnos después de la comilla inicial
            int end = start;
            // Avanza hasta la próxima comilla no escapada
            boolean escaped = false;
            while (end < body.length()) {
                char c = body.charAt(end);
                if (c == '"' && !escaped) break;
                // manejar barras invertidas consecutivas
                if (c == '\\') {
                    escaped = !escaped;
                } else {
                    escaped = false;
                }
                end++;
            }
            if (end <= body.length()) {
                String text = body.substring(start, end)
                        .replace("\\n", "\n")
                        .replace("\\\"", "\"")
                        .replace("\\\\", "\\");
                if (result.length() > 0) result.append("\n\n");
                result.append(text);
                foundAny = true;
            }
            from = end + 1;
        }
        return foundAny ? result.toString() : null;
    }
}
