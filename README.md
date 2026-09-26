# Prueba Técnica Developer – Parameta S.A.S

Servicio backend en **Java 17 (Spring Boot)** que expone un endpoint **REST**
para registrar empleados. El REST valida la información recibida y delega la
persistencia a un servicio web **SOAP** (contract-first, vía XSD), que es
quien finalmente guarda los datos en **MySQL**. La respuesta incluye,
además de los datos del empleado, su **tiempo de vinculación** a la
compañía y su **edad actual**, ambos expresados en años, meses y días.

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen)
![MySQL](https://img.shields.io/badge/MySQL-8.x-blue)
![SOAP](https://img.shields.io/badge/SOAP-contract--first-lightgrey)

## Tabla de contenido

- [Arquitectura](#arquitectura)
- [Tecnologías](#tecnologías)
- [Requisitos previos](#requisitos-previos)
- [Configuración de la base de datos](#configuración-de-la-base-de-datos)
- [Cómo ejecutar el proyecto](#cómo-ejecutar-el-proyecto)
- [Endpoint REST](#endpoint-rest)
- [Validaciones implementadas](#validaciones-implementadas)
- [Servicio SOAP](#servicio-soap)
- [Cómo probar con SoapUI](#cómo-probar-con-soapui)
- [Estructura del proyecto](#estructura-del-proyecto)
- [Pruebas unitarias](#pruebas-unitarias)
- [Autor](#autor)

## Arquitectura

```
Cliente
  │  GET /api/empleados?...
  ▼
EmpleadoController (REST)
  │
  ▼
EmpleadoService  ──►  valida: campos vacíos, formato de fechas,
  │                   mayoría de edad, número de documento duplicado
  ▼
EmpleadoSoapClient  ──►  arma y envía "registrarEmpleadoRequest"
  │            (http://localhost:8080/ws)
  ▼
EmpleadoEndpoint (servicio SOAP)  ──►  EmpleadoRepository (JPA)  ──►  MySQL
  │
  ▼
RegistrarEmpleadoResponse (id, mensaje, exitoso)
  │
  ▼
EmpleadoService calcula tiempoVinculacion y edadActual
  │
  ▼
EmpleadoResponseDTO (JSON) devuelto al cliente
```

El REST y el SOAP viven en la **misma aplicación Spring Boot** (un solo
`mvn spring-boot:run` levanta ambos), pero están desacoplados: el REST no
persiste directamente, solo habla con el cliente SOAP; quien escribe en la
base de datos es el endpoint SOAP.

El contrato SOAP está definido primero en un XSD (enfoque *contract-first*)
y se publica automáticamente como WSDL en:

```
http://localhost:8080/ws/empleados.wsdl
```

> **Nota sobre el estilo de código:** todo el proyecto está implementado con
> estructuras de control clásicas (`if/else`, `for`, excepciones propias),
> evitando Streams, expresiones lambda y encadenamientos funcionales de
> `Optional`.

## Tecnologías

- Java 17
- Spring Boot 3.x (Web, Web Services, Data JPA)
- Spring-WS (SOAP contract-first)
- MySQL 8.x
- Maven
- Lombok
- JUnit 5

## Requisitos previos

- JDK 17 instalado
- Maven 3.8+
- MySQL 8.x corriendo en `localhost:3306`

## Configuración de la base de datos

La URL JDBC en `src/main/resources/application.yml` incluye
`createDatabaseIfNotExist=true`, así que **no es obligatorio** crear la base
de datos manualmente: Hibernate crea la base y la tabla `empleados`
(`ddl-auto: update`) al arrancar la aplicación.

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/parameta_db?useSSL=false&serverTimezone=America/Bogota&createDatabaseIfNotExist=true
    username: root
    password: root
```

Ajusta usuario y contraseña según tu instalación local de MySQL.

## Cómo ejecutar el proyecto

```bash
git clone https://github.com/<tu-usuario>/<tu-repositorio>.git
cd <tu-repositorio>
mvn clean install
mvn spring-boot:run
```

La aplicación queda escuchando en `http://localhost:8080`.

## Endpoint REST

```
GET /api/empleados
```

| Parámetro          | Tipo   | Formato      | Obligatorio |
|--------------------|--------|--------------|-------------|
| nombres            | String | -            | Sí          |
| apellidos          | String | -            | Sí          |
| tipoDocumento      | String | -            | Sí          |
| numeroDocumento    | String | -            | Sí          |
| fechaNacimiento    | Date   | `yyyy-MM-dd` | Sí          |
| fechaVinculacion   | Date   | `yyyy-MM-dd` | Sí          |
| cargo              | String | -            | Sí          |
| salario            | Double | -            | Sí          |

### Ejemplo exitoso

```bash
curl "http://localhost:8080/api/empleados?nombres=Juan&apellidos=Perez&tipoDocumento=CC&numeroDocumento=123456789&fechaNacimiento=1990-05-15&fechaVinculacion=2020-03-01&cargo=Desarrollador&salario=4500000"
```

```json
{
  "id": 1,
  "nombres": "Juan",
  "apellidos": "Perez",
  "tipoDocumento": "CC",
  "numeroDocumento": "123456789",
  "fechaNacimiento": "1990-05-15",
  "fechaVinculacion": "2020-03-01",
  "cargo": "Desarrollador",
  "salario": 4500000.0,
  "tiempoVinculacion": { "anios": 6, "meses": 6, "dias": 23 },
  "edadActual": { "anios": 36, "meses": 4, "dias": 9 }
}
```

*(los valores de tiempo dependen de la fecha en que ejecutes la prueba)*

## Validaciones implementadas

| Validación                                   | Código de error              | HTTP |
|-----------------------------------------------|-------------------------------|------|
| Campo vacío o nulo                            | `CAMPO_VACIO`                  | 400  |
| Formato de fecha inválido (no `yyyy-MM-dd`)   | `FORMATO_FECHA_INVALIDO`       | 400  |
| Fecha futura o vinculación anterior a nacimiento | `FORMATO_FECHA_INVALIDO`    | 400  |
| Salario no numérico o menor/igual a cero      | `FORMATO_NUMERICO_INVALIDO`    | 400  |
| Empleado menor de edad (< 18 años)            | `EMPLEADO_MENOR_DE_EDAD`       | 400  |
| Número de documento ya registrado             | `NUMERO_DOCUMENTO_DUPLICADO`   | 409  |
| Error al invocar el servicio SOAP             | `ERROR_SERVICIO_SOAP`          | 502  |

Ejemplo de respuesta de error:

```json
{
  "codigo": "EMPLEADO_MENOR_DE_EDAD",
  "mensaje": "El empleado debe ser mayor de edad (18 años o más) para poder ser registrado",
  "timestamp": "2026-09-26T10:15:30"
}
```

La validación de **documento duplicado** tiene dos capas:

1. **Preventiva**, en `EmpleadoService`, mediante `existsByNumeroDocumento`
   antes de invocar el SOAP.
2. **De respaldo**, ante condiciones de carrera: la columna
   `numero_documento` tiene una restricción `UNIQUE` en la base de datos; si
   dos peticiones concurrentes pasan la validación anterior casi al mismo
   tiempo, MySQL rechaza el segundo `INSERT`, el endpoint SOAP traduce esa
   violación a un **SOAP Fault** de negocio, y el cliente SOAP lo traduce de
   vuelta a la misma excepción REST — el resultado es idéntico para quien
   consume la API.

## Servicio SOAP

- WSDL: `http://localhost:8080/ws/empleados.wsdl`
- Operación: `registrarEmpleadoRequest` → `registrarEmpleadoResponse`
- El REST lo invoca internamente; también se puede probar de forma directa
  con Postman o SoapUI.

## Cómo probar con SoapUI

1. Levanta la aplicación (`mvn spring-boot:run`) y confirma que
   `http://localhost:8080/ws/empleados.wsdl` carga en el navegador.
2. En SoapUI: **File → New SOAP Project**, pega esa URL en *Initial WSDL* y
   confirma.
3. En el árbol generado, abre la operación `registrarEmpleadoRequest` y
   reemplaza la plantilla con un XML como el siguiente:

```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                   xmlns:tns="http://parameta.com/empleado">
   <soapenv:Header/>
   <soapenv:Body>
      <tns:registrarEmpleadoRequest>
         <tns:nombres>Juan</tns:nombres>
         <tns:apellidos>Perez</tns:apellidos>
         <tns:tipoDocumento>CC</tns:tipoDocumento>
         <tns:numeroDocumento>123456789</tns:numeroDocumento>
         <tns:fechaNacimiento>1990-05-15</tns:fechaNacimiento>
         <tns:fechaVinculacion>2020-03-01</tns:fechaVinculacion>
         <tns:cargo>Desarrollador</tns:cargo>
         <tns:salario>4500000</tns:salario>
      </tns:registrarEmpleadoRequest>
   </soapenv:Body>
</soapenv:Envelope>
```

4. Envía la petición y revisa la respuesta XML, y opcionalmente confirma en
   MySQL con `SELECT * FROM parameta_db.empleados;`.

> El endpoint SOAP no repite las validaciones de negocio del REST (mayoría
> de edad, campos vacíos, etc.) — esas viven exclusivamente en
> `EmpleadoService`. El SOAP solo valida lo que el XSD exige (tipos y
> presencia de elementos) más la restricción de documento duplicado.

## Estructura del proyecto

```
src/main/java/.../
 ├─ Application.java
 ├─ config/           Configuración del cliente y servidor SOAP
 ├─ controller/       EmpleadoController (REST)
 ├─ service/          EmpleadoService (validaciones + orquestación)
 ├─ soap/client/      EmpleadoSoapClient (invoca el SOAP)
 ├─ soap/endpoint/    EmpleadoEndpoint (recibe SOAP y persiste)
 ├─ soap/model/       RegistrarEmpleadoRequest / Response (JAXB)
 ├─ entities/         Empleado (entidad JPA)
 ├─ repository/       EmpleadoRepository
 ├─ dto/              EmpleadoRequestDTO, EmpleadoResponseDTO, TiempoDTO, ErrorResponseDTO
 ├─ exception/        Excepciones de negocio + GlobalExceptionHandler
 └─ util/             CalculadoraTiempoUtil, DateConverterUtil
src/main/resources/
 ├─ application.yml
 ├─ xsd/empleado.xsd
 └─ sql/create-database.sql
```

## Pruebas unitarias

```bash
mvn test
```

## Autor

Desarrollado como prueba técnica para el proceso de selección de
**Parameta S.A.S**.
