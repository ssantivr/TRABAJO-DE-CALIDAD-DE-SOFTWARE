# 4. Gestión de cambios: respuesta formal al cliente

> Los datos entre corchetes `[ ]` son campos a completar. Las horas y fechas son **estimaciones propuestas** para validar con el cliente.

---

**Asunto:** Solicitud de cambio CR-001: incorporación de la operación de potencia, versión 1.1.0 de la Calculadora

**Para:** [Nombre del cliente], [Cargo / Organización]
**De:** [Nombre del responsable del proyecto], [Equipo / Organización]
**Fecha:** [dd/mm/aaaa]

Estimado/a [Nombre del cliente]:

Agradecemos su solicitud de agregar a la calculadora de consola una quinta operación: **potencia** (el número `a` elevado a la potencia `b`), implementada con un ciclo `for` y sin usar `Math.pow`. Esta solicitud llega después de la aprobación del documento de requisitos inicial (versión 1.0.0 de la línea base), por lo que la tratamos como un **cambio de alcance formal**. A continuación le presentamos su análisis.

## 1. Resumen del cambio

| Elemento | Línea base aprobada (v1.0.0) | Con el cambio (v1.1.0) |
|---|---|---|
| Operaciones | suma, resta, multiplicación, división | las cuatro anteriores **+ potencia** |
| Menú de consola y selector de operaciones de la GUI | 4 operaciones | 5 operaciones |
| Restricciones | enteros `int`; error controlado en división por cero | igual, y además errores controlados para exponente negativo y desbordamiento |

## 2. Impacto

- **Código:** un método nuevo `potencia(int base, int exponente)` en `Calculadora`, y una opción más en el menú de consola y en el selector de la GUI (ambos salen de la enumeración `Operacion`). Las operaciones existentes **no se modifican**, por lo que el riesgo de regresión es bajo.
- **Pruebas:** 11 casos nuevos para potencia (exponente positivo, cero, negativo, base cero, base negativa, desbordamiento) y 4 de interfaz (consola y GUI); se **re-ejecutan todos** los casos previos como prueba de regresión.
- **Documentación:** se actualizan el diagrama UML, la hoja de pruebas y el README.
- **Esfuerzo estimado:** 4 a 6 horas de trabajo (implementación 1 h, pruebas 2 h, documentación 1-2 h, revisión 1 h).
- **Plazo estimado:** [2] días hábiles desde la aprobación escrita de este documento.
- **Costo:** [sin costo adicional / valor estimado: $____], según el acuerdo comercial vigente.
- **Riesgos y mitigación:**
  - *Resultados que no caben en `int`* (por ejemplo 2^31): el sistema muestra un mensaje de error en lugar de entregar un valor incorrecto.
  - *Exponente negativo* (por ejemplo 2^-1 = 0,5): no es representable con enteros; se rechaza con un mensaje claro.
  - *0^0*: se define como 1 (convención matemática habitual).

## 3. Control de versiones y de configuración

1. **Congelamos** la línea base **v1.0.0** (etiqueta `v1.0.0`) con las cuatro operaciones; es la versión aprobada y siempre se podrá recuperar.
2. El cambio se desarrolla en una rama aparte, `feature/CR-001-potencia`, y se integra a la rama principal solo si pasa **todas** las pruebas.
3. Se publica la **v1.1.0** (versionado semántico: nueva funcionalidad compatible hacia atrás = incremento *minor*), con la etiqueta `v1.1.0`.
4. El cambio queda registrado en el control de cambios con identificador **CR-001**, solicitante, fecha, decisión y versión resultante.
5. Se actualiza el documento de requisitos a la versión 1.1, indicando qué requisito se añadió.

## 4. Qué puede esperar

- **Incluido:** operación de potencia con ciclo `for`, números enteros, mensajes de usuario en español, pruebas, documentación y paquete ejecutable (`.jar`).
- **No incluido:** exponentes negativos o fraccionarios con resultado decimal, números más grandes que `int`, interfaz gráfica. Cualquiera de ellos sería una **nueva solicitud de cambio**.
- **Criterios de aceptación:** (a) 100 % de los casos de prueba en estado *Pasa*, incluida la regresión de las cuatro operaciones originales; (b) `2 ^ 10` muestra `1024`; (c) los casos de error muestran un mensaje claro y el programa no se cierra inesperadamente.
- Mientras no recibamos su aprobación, **no iniciaremos la implementación**; la versión 1.0.0 sigue siendo la vigente.

## 5. Aprobación

Le solicitamos responder con su **aprobación o rechazo por escrito** (correo o firma de este documento). Quedamos atentos a cualquier ajuste y con gusto lo revisamos en una breve reunión.

Cordialmente,

[Nombre y firma]
[Cargo] · [Contacto]

| Decisión del cliente | ☐ Aprobado  ☐ Aprobado con ajustes  ☐ Rechazado |
|---|---|
| Nombre, firma y fecha | |
