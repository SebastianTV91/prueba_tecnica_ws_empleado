# Prueba Técnica Developer

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

- [Tecnologías](#tecnologías)
- [Requisitos previos](#requisitos-previos)
- [Configuración de la base de datos](#configuración-de-la-base-de-datos)
- [Endpoint REST](#endpoint-rest)
- [Servicio SOAP](#servicio-soap)
- [Cómo probar con SoapUI](#cómo-probar-con-soapui)

## Tecnologías

- Java 17
- Spring Boot 3.x (Web, Web Services, Data JPA)
- Spring-WS (SOAP contract-first)
- MySQL 8.x
- Maven
- Lombok

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
    url: jdbc:mysql://localhost:3306/bd_empresa?useSSL=false&serverTimezone=America/Bogota&createDatabaseIfNotExist=true
    username: root
    password: root
```

Ajusta usuario y contraseña según tu instalación local de MySQL.

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
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:emp="http://parameta.com/empleado">
   <soapenv:Header/>
   <soapenv:Body>
      <emp:registrarEmpleadoRequest>
         <emp:nombres>María Alejandra</emp:nombres>
         <emp:apellidos>Rodríguez Silva</emp:apellidos>
         <emp:tipoDocumento>DNI</emp:tipoDocumento>
         <emp:numeroDocumento>1023456790</emp:numeroDocumento>
         <emp:fechaNacimiento>1988-11-22</emp:fechaNacimiento>
         <emp:fechaVinculacion>2026-03-15</emp:fechaVinculacion>
         <emp:cargo>Gerente de Proyectos</emp:cargo>
         <emp:salario>4500000</emp:salario>
      </emp:registrarEmpleadoRequest>
   </soapenv:Body>
</soapenv:Envelope>
```

4. Envía la petición y revisa la respuesta XML, y opcionalmente confirma en
   MySQL con `SELECT * FROM parameta_db.empleados;`.

> El endpoint SOAP no repite las validaciones de negocio del REST (mayoría
> de edad, campos vacíos, etc.) — esas viven exclusivamente en
> `EmpleadoService`. El SOAP solo valida lo que el XSD exige (tipos y
> presencia de elementos) más la restricción de documento duplicado.
