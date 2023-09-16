<h1 align="center">Live Football World Cup Score Board</h1>
<p align="center">
    This is the repository for all integrations to get the Live Football World Cup application:<br/>
  <ul>
    <li>✅ Start a Football Match</li>
    <li>✅ Update a Football Match</li>
    <li>✅ Finish a Football Match</li>
    <li>✅ View the Live Dashboard</li>
  </ul>
</p>

# Requirements

* Java 17 and onwards
* Kotlin

# Set up your app

## Gradle(`build.gradle.kts`)

In order to use this library, just add the following dependency:

```
implementation("no.sportradar.football:sportradar-dashboard")
```

## Maven(`pom.xml`)

In order to use this library, just add the following dependency:

```
<dependency>
  <groupId>no.sportradar.football</groupId>
  <artifactId>sportradar-dashboard</artifactId>
  <version>LATEST</version>
</dependency>
```

# Integrations

Currently, we have only an Interface available to use all the functionalities.

Define the service, first:

```
     FootballMatchService footballMatchService = new FootballMatchServiceImpl();
```
### Start a Match

This service is used to start a match with default scores as 0.
Any attempt with either of the team names is blank or any existing match or any team already playing results in `IllegalArgumentException`.

Use the client as follows:

```
    footballMatchService.startMatch(homeTeamName, awayTeamName)
```

### Update a Match

This service is used to update an existing match. Any attempt with non-existing match results in an `NoMatchFoundException`.

Use the client as follows:

```
    footballMatchService.updateMatchDetails(FootballMatch(...))
```

### Finish a Match

This service is used to finish an existing match. Any attempt with non-existing match results in an `NoMatchFoundException`.

Use the client as follows:

```
    footballMatchService.finishMatch(FootballMatch(...))
```

### View the Live Dashboard

This service is used to view the live dashboard details of Football World Cup.
This will sort the dashboard with most recent match and highest score combination.

Use the client as follows:

```
    footballMatchService.getSummaryDashboard()
```
