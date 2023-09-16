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
}