package no.sportradar.dashboard

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

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
}