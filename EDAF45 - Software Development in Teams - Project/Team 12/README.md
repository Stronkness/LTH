# Race-clock
Programmet är uppdelat i subprojekten: register och result.

Mycket av programmet kan ändras genom att ändra parametrarna i konfigfilen vilken är en JSON-fil. 

## Register
Inuti register mappen finns två mappar som innehåller tester respektive main-mappen för sub-projektet. Main-mappen för Register innehåller filer som har hand om registreringsprogrammet samt innehåller GUI för registreringen. Register sköter registreringar via GUI, hanterar sena registreringar till det specifika loppet och erbjuder en användarvänlig GUI för användaren.


## Result
Result har en util mapp som främst används för att läsa och skriva till filer. De saker som är specifika för ett vissttyp av lopp ligger i mappen <LOPPTYP> i result. Där finns t.ex. klasser som formaterar utskrifterna. Där finns också en mapp error som hanterar de olika felen som kan uppstå. Saker som är generella för alla tävlingstyper ligger i result.


Väl inuti result-mappen finns alla filer som har hand om att ge ut resultatet till användaren med tider, måltider, startider etc. Hädanefter finns det fyra mappar: maraton, varvlopp, etapplopp och view. View sköter GUI för resultatprogrammet där användaren ska kunna välja startfiler, måltidfiler och namnfiler för att kunna generera ett resultat i form av vilket lopp som väljs. De resterande mapparna maraton, varvlopp och etapplopp innehåller filerna för att hantera de specifika loppen. Varje lopptyp har en error mapp vars syfte är att hantera felhantering kring lopptypen. För att mer info om hur man använder programmet, se i maunelen som ligger i dokumentationsmappen.
