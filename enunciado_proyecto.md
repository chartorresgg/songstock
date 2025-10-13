PIF 2025-2 
SongStock Market Place 
Desarrollo Web. Yamid Ramírez, MSc. 
Propósito 
El número de tiendas virtuales de canciones en internet se ha incrementado en los 
últimos tiempos pero cada una busca la forma de diferenciarse para atraer distintos 
nichos de mercado. La aplicación SongStock se ha venido posicionando bastante 
bien en el mundo de la música por internet. Sin embargo, sus creadores quieren 
atraer un público que últimamente ha venido creciendo y despertando gran interés 
para el mercado: los coleccionadores de discos de vinilo.  
Actualmente SongStock permite visualizar un catálogo de discos y canciones que 
la tienda tiene a la venta. Cada disco tiene un nombre, un género y una imagen de 
la carátula del disco. La tienda se especializa en la venta de canciones en formato 
MP3 por lo que ofrece toda la información relevante de una canción al usuario. Esta 
información comprende: el nombre de la canción, su precio individual, la duración 
en minutos y segundos, el tamaño en megabytes (MB) y la calidad de la canción 
expresada en kilobytes por segundo (Kbps).  
Una característica importante del sistema actual es que permite manejar 
recopilaciones creadas por los mismos usuarios. Una recopilación puede ser 
entendida como una lista de reproducción compuesta por canciones de diferentes 
géneros, autores, discos, duración, etc. Un usuario puede construir su propia 
recopilación a partir de las canciones que estén a la venta en el sistema. Es posible 
buscar recopilaciones de otros usuarios si estos establecen que son públicas. Esto 
con el fin de que otros usuarios puedan crear sus propias recopilaciones tomando 
inicialmente las canciones contenidas en recopilaciones publicadas por otras 
personas.  
Nuevos Requerimientos 
Debido al auge que durante los últimos tiempos ha tenido la compraventa de 
vinilos usados, se decidió extender la funcionalidad de SongStock para:  
1. Proporcionar al usuario aficionado a la música la posibilidad de adquirir 
discos de vinilo que contengan algunas de las canciones que se consiguen 
fácilmente en formato MP3. 
2. Permitir a los coleccionistas de vinilos vender sus preciadas posesiones en 
un ambiente Web. Para esto, al momento de listar las canciones 
almacenadas en el sistema, junto con la opción de adicionar al carrito de 
compras, para cada canción, el sistema debe desplegar la opción de 
visualizar y agregar al carrito el vinilo (o los) en donde dicha canción esté 
contenida. Es claro que para aquellas canciones que no aparezcan 
relacionadas en ningún vinilo sólo debe aparecer la opción de adicionar al 
carrito en formato mp3. De igual forma, si un disco en mp3 en SongStock 
tiene una versión exactamente igual en vinilo, el sistema debe permitir no 
solamente agregar al carrito el disco completo en formato mp3, sino también 
agregar el disco en formato vinilo.  
3. Consultar y comprar vinilos directamente sin tener que pasar por el proceso 
de búsqueda de canciones o discos en mp3.  
4. Una vez el proveedor haya enviado el vinilo físico en su domicilió, debe 
registrar en el sistema dicho envío. 
5. Más adelante, una vez que el disco sea recibido, el usuario comprador tiene 
la posibilidad de valorar la transacción comercial, es decir, emitir un concepto 
de satisfacción o no acerca del proceso de compra y envío. 
Con respecto al usuario coleccionista proveedor de vinilos, el sistema debe 
permitir:  
1. Registrar el proveedor frente al Marketplace de SongStock 
2. Autenticar en el sistema al proveedor. 
3. Construir un catálogo de vinilos. En el sistema, cada vinilo del catálogo debe 
caracterizarse por un nombre, un artista, el año de salida al mercado 
discográfico, el conjunto de canciones que lo componen, un precio 
determinado y una cantidad de unidades en inventario. Por obvias razones, 
a diferencia de un disco en mp3, las canciones de un vinilo no se pueden 
vender por separado. Cada canción del vinilo está caracterizada por un 
nombre y una duración. Si un vinilo contiene una canción previamente 
registrada en el sistema, el sistema debe identificar este hecho y permitirle al 
proveedor asociar automáticamente las canciones del vinilo a las canciones 
registradas. 
4. Consultar órdenes de pedido de vinilos vía Web. Cada pedido debe tener la 
información del usuario que hizo la respectiva compra (nombre, correo y 
número de compras en el sistema), al igual que el medio de pago. Para 
facilitar la recepción de pedidos, cada vez que se genera un pedido en el 
Marketplace, la información asociada debe ser enviada al correo registrado 
por el proveedor al momento de su dada de alta en el sistema.  
5. Confirmar o rechazar la orden de pedido. Un proveedor puede aceptar o 
rechazar una orden de pedido vía Web. En ambos casos se le debe enviar 
un correo electrónico al usuario comprador con la información de aceptación 
o rechazo. Todo rechazo debe tener asociada una observación. Si la orden 
de pedido es aceptada, el proveedor debe indicar la fecha estimada de envío 
del pedido. 
6. Consultar, tanto los usuarios compradores como los proveedores, en 
cualquier momento los reportes de las compras o ventas realizadas. Este 
reporte también debe mostrar las ventas abiertas (en donde el usuario 
comprador no ha confirmado la recepción de una orden de pedido o en donde 
el proveedor no ha confirmado la confirmación o el rechazo de la orden de 
pedido). 
Otros Requerimientos y Restricciones 
1. La información de la tienda debe ser persistente y el proceso debe ser 
completamente transparente para el usuario. 
2. Al momento de que el usuario inicie sesión, se debe saber qué discos y 
canciones ha comprado, y cuáles son las recopilaciones que ha creado y 
publicado.