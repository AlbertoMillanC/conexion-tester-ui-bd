# Actividad 3 - Incorporando UI y BD (Inventario IU Digital)

Trabajo de la Unidad 3 de Programación Orientada a Objetos II - IU Digital.

**Estudiante:** Carlos Alberto Millán Castaño  
**Grupo:** POII-ACTIVIDADES 8

## Descripción
Aplicación de escritorio en Java Swing y JDBC (`edu.iudigital.ea3`) con dos pestañas:
- **Pestaña Conexión (`PanelConexion` / `ProbadorConexion`):** permite configurar el motor de base de datos, la URL JDBC (`jdbc:sqlite:inventario.db`), el usuario y la contraseña, probando la conexión con `DriverManager.getConnection()`, cerrando recursos con `try-with-resources` y mostrando mensajes en verde (éxito) o rojo (`ClassNotFoundException` y `SQLException`).
- **Pestaña Inventario (`PanelInventario` / `ProductoDao` / `ExportadorCsv`):** se habilita al conectar la base de datos y permite buscar, crear, editar, eliminar productos del inventario y exportar la tabla al archivo `inventario.csv`.
