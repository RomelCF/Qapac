# QhapaqÑan 

**Autor:** Romel Rodrigo Chumpitaz Flores  
**Código:** 20231014A  
**Carrera:** Ingeniería de Software – Universidad Nacional de Ingeniería (UNI)  
**Curso:** Construcción de Software  

---

## Descripción del Proyecto  
**QhapaqÑan** es una aplicación web desarrollada como parte del curso **Construcción de Software**, enfocada en la **venta de pasajes de bus interprovinciales**.  
El sistema busca digitalizar el proceso de compra de pasajes, brindando a los usuarios una experiencia rápida, ordenada y segura para planificar sus viajes.  

El nombre *QhapaqÑan* —que significa *Gran Camino Inca*— simboliza la conexión entre distintas regiones del Perú, reflejando el propósito de la aplicación: **unir destinos a través de la tecnología**.

---

## Tecnologías Utilizadas  

### Frontend  
- **React + Vite:** para un desarrollo ágil y una interfaz moderna.  
- **TypeScript:** mejora la mantenibilidad y la detección temprana de errores.  
- **HTML5, CSS3 y JavaScript:** base del diseño e interacción.  
- **Axios:** para la comunicación con el backend mediante API REST.  

### Backend  
- **Java (Spring Boot):** encargado de la lógica de negocio y los servicios REST.  
- **MySQL:** base de datos relacional para la persistencia de la información.  
- **Spring Data JPA:** para la gestión eficiente de las entidades y repositorios.  

---

## Funcionalidades Implementadas  

- **Registro de usuarios y empresas de transporte.**  
- **Búsqueda de rutas (origen – destino – fecha).**  
- **Selección de asiento y compra (simulada).**  
- **Generación de comprobante electrónico.**  
- **Panel de control para ventas y reportes.**  

---

## Estadísticas del Proyecto  
Distribución de lenguajes según GitHub:  
- **TypeScript:** 68.6%  
- **Java:** 30.6%  
- **Otros:** 0.8%  

---

## Ejecución del Proyecto

### 1. Base de Datos (MySQL con XAMPP)

1. Abre **XAMPP Control Panel**.  
2. Inicia los módulos **Apache** y **MySQL**.  
3. En tu navegador, abre:  
   [http://localhost/phpmyadmin](http://localhost/phpmyadmin)  
4. Crea una base de datos llamada `qhapaqnan`.  
5. Importa los archivos SQL del proyecto ubicados en la carpeta `/database`:
   - **Primero**: Importa `Qapac.sql` (estructura de la base de datos).
   - **Segundo**: Importa `Qapac_Inserts.sql` (datos iniciales).

---

### 2. Frontend (React + Vite)

1. Abre una nueva terminal en la carpeta del **frontend**:
```bash
   cd QhapaqNan/frontend
```

2. Instala las dependencias:
```bash
   npm install
```

3. Inicia el servidor de desarrollo:
```bash
   npm run dev
```

4. Abre tu navegador y accede a la URL que muestra la consola (por defecto):  
   [http://localhost:5173](http://localhost:5173)

---

### 3. Backend (Spring Boot)

1. Abre otra terminal en la carpeta del **backend**:
```bash
   cd QhapaqNan/backend
```

2. Asegúrate de que la base de datos `qhapaqnan` esté activa en XAMPP.

3. Configura el archivo `src/main/resources/application.properties` con tus credenciales locales:
```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/qhapaqnan
   spring.datasource.username=root
   spring.datasource.password=
   spring.jpa.hibernate.ddl-auto=update
   spring.jpa.show-sql=true
```

4. Ejecuta el backend con:
```bash
   mvn spring-boot:run
```

5. El backend estará disponible en:  
   [http://localhost:8080](http://localhost:8080)

---

## Objetivos del Proyecto

- Digitalizar el proceso de compra de pasajes interprovinciales.
- Brindar una experiencia de usuario moderna e intuitiva.
- Aplicar principios de ingeniería de software y arquitectura limpia.
- Integrar backend robusto con frontend dinámico y eficiente.

---

## Estructura del Proyecto
```
Qapac/
├── frontend/          # Aplicación React + Vite
├── backend/           # API REST con Spring Boot
└── database/          # Scripts SQL
    ├── Qapac.sql      # Estructura de la base de datos
    └── Qapac_Inserts.sql  # Datos iniciales
```

