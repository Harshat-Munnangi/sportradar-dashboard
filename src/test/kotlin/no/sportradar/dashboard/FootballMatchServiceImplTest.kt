package no.sportradar.dashboard

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class FootballMatchServiceImplTest {

    private lateinit var footballMatchServiceImpl: FootballMatchServiceImpl

    @BeforeEach
    fun setUp() {
        footballMatchServiceImpl = FootballMatchServiceImpl()
    }

    @Test
    fun `should add teams to the dashboard with 0 as starting points` () {
        footballMatchServiceImpl.startMatch("HomeTeam1", "AwayTeam1")
        footballMatchServiceImpl.startMatch("HomeTeam2", "AwayTeam2")
        footballMatchServiceImpl.startMatch("HomeTeam3", "AwayTeam3")
        footballMatchServiceImpl.startMatch("HomeTeam4", "AwayTeam4")
        footballMatchServiceImpl.startMatch("HomeTeam5", "AwayTeam5")
        footballMatchServiceImpl.startMatch("HomeTeam6", "AwayTeam6")
        val matchList = footballMatchServiceImpl.getSummaryDashboard()
        assertThat(matchList.size).isEqualTo(6)
        matchList.forEach {
            assertThat(it.homeTeamPoints).isEqualTo(0)
            assertThat(it.awayTeamPoints).isEqualTo(0)
        }
    }

    @Test
    fun `should throw error if team name is empty` () {
        footballMatchServiceImpl.startMatch("HomeTeam1", "AwayTeam1")
        footballMatchServiceImpl.startMatch("HomeTeam2", "AwayTeam2")
        assertThrows<IllegalArgumentException> {
            footballMatchServiceImpl.startMatch("HomeTeam3", "")
        }
        footballMatchServiceImpl.startMatch("HomeTeam4", "AwayTeam4")
        assertThat(footballMatchServiceImpl.getSummaryDashboard().size).isEqualTo(3)
    }

    @Test
    fun `should throw error if match already exists` () {
        footballMatchServiceImpl.startMatch("HomeTeam1", "AwayTeam1")
        footballMatchServiceImpl.startMatch("HomeTeam2", "AwayTeam2")
        footballMatchServiceImpl.startMatch("HomeTeam3", "AwayTeam3")
        assertThrows<MatchAlreadyExistsException> {
            footballMatchServiceImpl.startMatch("HomeTeam2", "AwayTeam2")
        }
        footballMatchServiceImpl.startMatch("HomeTeam4", "AwayTeam4")
        assertThat(footballMatchServiceImpl.getSummaryDashboard().size).isEqualTo(4)
    }

    @Test
    fun `should throw error if any team is busy in other match` () {
        footballMatchServiceImpl.startMatch("HomeTeam1", "AwayTeam1")
        footballMatchServiceImpl.startMatch("HomeTeam2", "AwayTeam2")
        assertThrows<TeamAlreadyBusyException> {
            footballMatchServiceImpl.startMatch("HomeTeam3", "AwayTeam1")
        }
        footballMatchServiceImpl.startMatch("HomeTeam4", "AwayTeam4")
        assertThat(footballMatchServiceImpl.getSummaryDashboard().size).isEqualTo(3)
    }

    @Test
    fun `should update a match score` () {
        footballMatchServiceImpl.startMatch("HomeTeam1", "AwayTeam1")
        footballMatchServiceImpl.startMatch("HomeTeam2", "AwayTeam2")
        assertThat(footballMatchServiceImpl.getSummaryDashboard().size).isEqualTo(2)
        val modifiedMatch = FootballMatch("HomeTeam2", 4, "AwayTeam2", 7)
        footballMatchServiceImpl.updateMatchDetails(modifiedMatch)
        val updatedMatchList = footballMatchServiceImpl.getSummaryDashboard()
        assertThat(updatedMatchList.size).isEqualTo(2)
        updatedMatchList
            .first { it.homeTeamName == modifiedMatch.homeTeamName && it.awayTeamName == modifiedMatch.awayTeamName }
            .let {
                assertThat(it.homeTeamPoints).isEqualTo(modifiedMatch.homeTeamPoints)
                assertThat(it.awayTeamPoints).isEqualTo(modifiedMatch.awayTeamPoints)
            }
    }

    @Test
    fun `should throw error if attempt to update a non-existed match` () {
        footballMatchServiceImpl.startMatch("HomeTeam1", "AwayTeam1")
        footballMatchServiceImpl.startMatch("HomeTeam2", "AwayTeam2")
        val nonExistingMatch = FootballMatch("HomeTeam12", 5, "AwayTeam1", 7)
        assertThrows<NoMatchFoundException> { footballMatchServiceImpl.updateMatchDetails(nonExistingMatch) }
        assertThat(footballMatchServiceImpl.getSummaryDashboard().size).isEqualTo(2)
    }

    @Test
    fun `should remove a match once it is finished` () {
        footballMatchServiceImpl.startMatch("HomeTeam11", "AwayTeam12")
        footballMatchServiceImpl.startMatch("HomeTeam21", "AwayTeam22")
        assertThat(footballMatchServiceImpl.getSummaryDashboard().size).isEqualTo(2)
        footballMatchServiceImpl.finishMatch(FootballMatch(homeTeamName = "HomeTeam21", awayTeamName = "AwayTeam22"))
        val newMatchList = footballMatchServiceImpl.getSummaryDashboard()
        assertThat(newMatchList.size).isEqualTo(1)
        assertThat(newMatchList.first().homeTeamName).isEqualTo("HomeTeam11")
        assertThat(newMatchList.first().awayTeamName).isEqualTo("AwayTeam12")
    }

    @Test
    fun `should throw error if attempt to finish a non-existed match` () {
        footballMatchServiceImpl.startMatch("HomeTeam11", "AwayTeam12")
        footballMatchServiceImpl.startMatch("HomeTeam21", "AwayTeam22")
        assertThat(footballMatchServiceImpl.getSummaryDashboard().size).isEqualTo(2)
        assertThrows<NoMatchFoundException> {
            footballMatchServiceImpl.finishMatch(FootballMatch(homeTeamName = "HomeTeam2", awayTeamName = "AwayTeam22"))
        }
        assertThat(footballMatchServiceImpl.getSummaryDashboard().size).isEqualTo(2)
    }
}