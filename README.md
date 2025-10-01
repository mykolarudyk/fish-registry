ENGELSK:
1) How to set up and run the application

Prerequisites:
Java 21 (project uses Spring Boot 3.5.6)
Node.js (project uses 22.18), npm (10.9.3)
Angular (20.1.6)

git clone https://github.com/mykolarudyk/fish-registry.git
cd fish-registry

# install concurrently
npm install

cd frontend
npm install
cd ..

npm run dev

Backend: http://localhost:8080
Frontend: http://localhost:4200
H2 console: http://localhost:8080/h2

2) How the code is structured and the rationale
Backend structure - Maven standart layout

backend/
└─ src/
   ├─ main/java/com/example/backend/
   │  ├─ BackendApplication.java
   │  ├─ BootstrapData.java                   # Seeds mock fish records on first run
   │  ├─ common/CorsConfig.java               # CORS (allow requests from FE)
   │  └─ fish/
   │     ├─ api/
   │     │  ├─ FishController.java            # REST endpoints under /api/fish
   │     │  └─ FishDtos.java                  # DTOs + validation
   │     ├─ app/
   │     │  └─ FishService.java               # Application/service layer + 404 mapping
   │     ├─ domain/
   │     │  └─ Fish.java                      # JPA entity with constraints
   │     └─ infra/
   │        └─ FishRepository.java            # Spring Data JPA repository
   └─ test/java/com/example/backend/
      ├─ BackendApplicationTests.java
      └─ fish/
         ├─ api/FishControllerTest.java
         ├─ app/FishServiceTest.java
         └─ infra/FishRepositoryTest.java

-api  - contains controllers and DTOs
-app - orchestration/business logic
-domain - JPA entities
-infra - data access via Spring Data JPA

Frontend structure

frontend/
└─ src/
   ├─ app/
   │  ├─ app.ts / app.html / app.routes.ts
   │  ├─ components/
   │  │  └─ fish/
   │  │     └─ fish-list/
   │  │        ├─ fish-list.component.ts
   │  │        ├─ fish-list.component.html
   │  │        └─ fish-list.component.scss
   │  ├─ models/
   │  │  ├─ fish.model.ts                # interface
   │  │  └─ page.model.ts             # Page interface for backend pagination
   │  ├─ services/
   │  │  └─ fish.service.ts               # HTTP client
   └─ environments/
      └─ environment.ts                   # apiBase

- Standart Angular structure
- 3 folders - components, models and services for separation of concerns.
When app grows - for example fish, cow, sheep - then it will be better to create features folder and store corresponding files inside respective subfolders.
 - components - UI only
 - services - data access
 - models - types only
 - I use Angular Material (forms, paginator, sorting out of the box) and Reactive forms

3) What tools are used for testing

BE testing

JUnit 5 (Jupiter) — test framework
Spring Boot Test — test slices (@DataJpaTest, @WebMvcTest), MockMvc.
Mockito — mocking service/repository

# Run tests BE
cd backend
./mvnw test

FE testing

Jasmine — test framework.
Karma — test runner (Angular CLI).
Angular testing utilities — TestBed, HttpClientTestingModule, NoopAnimationsModule, fakeAsync/tick.

# Run tests FE
cd frontend
ng test

NORSK:
1) Slik setter du opp og kjører applikasjonen

Forutsetninger:
Java 21 (prosjektet bruker Spring Boot 3.5.6)
Node.js (prosjektet bruker 22.18), npm (10.9.3)
Angular (20.1.6)

git clone https://github.com/mykolarudyk/fish-registry.git
cd fish-registry

# installer 'concurrently'
npm install

cd frontend
npm install
cd ..

npm run dev

Backend: http://localhost:8080
Frontend: http://localhost:4200
H2-konsoll: http://localhost:8080/h2

2) Hvordan koden er strukturert og begrunnelsen bak strukturen.

Backend-struktur – standard Maven-oppsett

backend/
└─ src/
   ├─ main/java/com/example/backend/
   │  ├─ BackendApplication.java
   │  ├─ BootstrapData.java                   # Fyller med eksempeldata for fisk ved første kjøring
   │  ├─ common/CorsConfig.java               # CORS (tillater forespørsler fra frontend)
   │  └─ fish/
   │     ├─ api/
   │     │  ├─ FishController.java            # REST-endepunkter under /api/fish
   │     │  └─ FishDtos.java                  # DTO-er + validering
   │     ├─ app/
   │     │  └─ FishService.java               # Applikasjons-/tjenestelag + 404-håndtering
   │     ├─ domain/
   │     │  └─ Fish.java                      # JPA-entitet med begrensninger
   │     └─ infra/
   │        └─ FishRepository.java            # Spring Data JPA-repositorium
   └─ test/java/com/example/backend/
      ├─ BackendApplicationTests.java
      └─ fish/
         ├─ api/FishControllerTest.java
         ├─ app/FishServiceTest.java
         └─ infra/FishRepositoryTest.java


api – inneholder controllere og DTO-er
app – orkestrering/forretningslogikk
domain – JPA-entiteter
infra – datatilgang via Spring Data JPA

Frontend-struktur

frontend/
└─ src/
   ├─ app/
   │  ├─ app.ts / app.html / app.routes.ts
   │  ├─ components/
   │  │  └─ fish/
   │  │     └─ fish-list/
   │  │        ├─ fish-list.component.ts
   │  │        ├─ fish-list.component.html
   │  │        └─ fish-list.component.scss
   │  ├─ models/
   │  │  ├─ fish.model.ts                # interface
   │  │  └─ page.model.ts                # Page-interface for backend-paginering
   │  ├─ services/
   │  │  └─ fish.service.ts              # HTTP-klient
   └─ environments/
      └─ environment.ts                  # apiBase


Standard Angular-struktur

3 mapper – components, models og services for «separation of concerns».
Når appen vokser – f.eks. fish, cow, sheep – er det bedre å opprette en features-mappe og legge tilhørende filer i respektive undermapper.
components – kun UI
services – datatilgang
models – kun typer
Jeg bruker Angular Material (skjemaer, paginator, sortering «out of the box») og Reactive Forms.

3) Hvilke verktøy brukes til testing
Backend-testing (BE)

JUnit 5 (Jupiter) — test-rammeverk
Spring Boot Test — test-slices (@DataJpaTest, @WebMvcTest), MockMvc
Mockito — mocking av service/repository

Kjør tester – backend

cd backend
./mvnw test

Frontend-testing (FE)

Jasmine — test-rammeverk
Karma — test-runner (Angular CLI)
Angular testing utilities — TestBed, HttpClientTestingModule, NoopAnimationsModule, fakeAsync/tick

Kjør tester – frontend

cd frontend
ng test



