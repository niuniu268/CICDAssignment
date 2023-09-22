# CICD Assignment
## Aim of the project
- Mock används för att
    - att skapa svar från färdmedelsleverantörer
    - att ge respons på betalningsförsök
- Unit testning används för att testa de kritiska klasserna i applikationen.
- Smoke testning används för att avgöra att kommunikationen mellan klasserna fungerar.
- Integration testing används för att se så att information om autentiserade användare kan sparas till databasen.
- End-to-end testing används för att testa så att varje endpoint ger det svar som förväntas
- Jenkins hämtar samtlig kod från ett github repo
- Jenkins används för att skapa två pipelines:
    - Första pipeline:n kör testerna mot utvecklingsmiljön
    - Andra pipeline:n kör testerna i produktionsmiljö (ubuntu 22 eller alpine) samt kör artefakten i java-runtime.
- För ett högre betyg så måste gränsfall till större delar hanteras i programmet och i testerna.
