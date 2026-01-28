# 🌲 Trail Tracker API - Core Service (Spring Boot)

Este repositorio contiene el núcleo lógico, la persistencia y la infraestructura de seguridad del sistema **Trail Tracker**. Diseñado bajo los estándares de **Clean Architecture**, garantiza un sistema desacoplado, escalable y preparado para entornos de nube como **Azure**.

🔗 **Repositorio Frontend:** https://github.com/JyhsKaiser/trail-tracker-forntend.git

---

## 🛠️ Stack Tecnológico
* **Lenguaje:** Java 21 (Long Term Support).
* **Framework:** Spring Boot 3.x con Spring Security.
* **Base de Datos:** MySQL 8.4 para persistencia de datos.
* **Arquitectura:** Clean Architecture (Separación estricta de Dominio, Aplicación e Infraestructura).
* **Gestor de Dependencias:** Maven.

---

## 🛡️ Blindaje de Seguridad (Enterprise Level)
Nuestra implementación de seguridad aplica múltiples capas de protección para garantizar la integridad de los datos:
* **Identidad Inmutable:** El sistema bloquea cambios en el `username` para asegurar la integridad de los tokens de sesión y registros de auditoría.
* **JWT (Stateless Authentication):** Gestión de sesiones mediante JSON Web Tokens con validación estricta en cada petición HTTP.
* **Defensa CSRF/XSRF:** Implementación de `CookieCsrfTokenRepository` configurado específicamente para SPAs, obligando al cliente a validar su identidad en cada operación de escritura (`POST`, `PATCH`, `DELETE`).
* **CORS Restrictivo:** Configuración de orígenes y métodos controlados, permitiendo credenciales seguras solo desde dominios autorizados.
* **Seguridad de Credenciales:** Uso de `BCrypt` para el almacenamiento seguro de contraseñas y validación obligatoria antes de cualquier cambio de clave.

---

## 🐳 Dockerización e Infraestructura
* **Multi-stage Build:** Dockerfile optimizado que separa la compilación de la ejecución, reduciendo la superficie de ataque y el tamaño de la imagen final.
* **Persistencia:** Uso de volúmenes de Docker (`trail_data`) para asegurar que la información no se pierda al reiniciar o eliminar contenedores.
* **Seguridad de Ejecución:** El servicio corre bajo un usuario no-root (`trailuser`) para mitigar riesgos de seguridad en el contenedor.
* **Orquestación:** Configuración de red interna (`trail-net`) para comunicación segura entre servicios.

---

## 🚀 Instrucciones de Levantamiento (Ecosistema Completo)

Para que el sistema funcione correctamente, los contenedores deben coexistir en la misma red virtual de Docker.

### 1. Requisitos Previos
* **Docker Desktop** instalado y en ejecución.
* **Git** para la clonación de repositorios.

### 2. Estructura de Carpetas
Clonar ambos repositorios en carpetas hermanas para que el orquestador encuentre los contextos de construcción:

