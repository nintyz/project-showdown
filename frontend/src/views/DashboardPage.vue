<template>
  <div class="dashboard-container">
    <!-- Navbar -->
    <Navbar />

    <!-- Tournament Details Section -->
    <div class="tournament-details container my-4">
      <h2 class="text-center">{{ tournament.name }}</h2>
      <p class="text-center">{{ tournament.venue }} | {{ tournament.date }}</p>
    </div>

    <div class="content container-fluid mx-5">
      <div class="row">
        <!-- Left Section: Tournament Bracket -->
        <div class="col-lg-8 col-md-7">
          <h4>Current Tournament Bracket</h4>
          <TennisTournamentBracket />
        </div>

        <!-- Right Section: Live Matches -->
        <div class="col-lg-4 col-md-5 live-matches">
          <h4>Live Matches</h4>
          <div v-for="(match, index) in liveMatches" :key="index" class="live-match card mb-3">
            <div class="card-body">
              <div class="match-category d-flex align-items-center">
                <span>{{ match.category }}</span>
                <span class="ms-auto text-muted">Duration: {{ match.duration }}</span>
              </div>
              <div class="players mt-3">
                <div class="player d-flex justify-content-between align-items-center">
                  <img :src="match.player1.flag" alt="Player 1 Flag" class="flag me-2" />
                  <span>{{ match.player1.name }}</span>
                  <span>{{ match.player1.score }}</span>
                </div>
                <div class="player d-flex justify-content-between align-items-center">
                  <img :src="match.player2.flag" alt="Player 2 Flag" class="flag me-2" />
                  <span>{{ match.player2.name }}</span>
                  <span>{{ match.player2.score }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import TennisTournamentBracket from '@/components/TennisTournamentBracket.vue';
import Navbar from '@/components/NavbarComponent.vue';
import '@/assets/main.css'; 
export default {
  components: {
    TennisTournamentBracket,
    Navbar,
  },
  data() {
    return {
      tournament: {
        name: "Grand Slam Tournament",
        venue: "London, UK",
        date: "1 - 14 July 2024"
      },
      liveMatches: [
        {
          category: "Women's Singles",
          player1: { name: "Player A", score: 7, flag: "https://flagcdn.com/w320/sg.png" },
          player2: { name: "Player B", score: 5, flag: "https://flagcdn.com/w320/sg.png" },
          duration: "1:53",
        },
        {
          category: "Men's Singles",
          player1: { name: "Player C", score: 6, flag: "https://flagcdn.com/w320/us.png" },
          player2: { name: "Player D", score: 4, flag: "https://flagcdn.com/w320/fr.png" },
          duration: "1:23",
        },
      ],
    };
  }
};
</script>

<style scoped>
.dashboard-container {
  background-color: #f3eeea;
  min-height: 100vh;
}

.tournament-details {
  background-color: #ebe3d5;
  padding: 20px;
  border-radius: 10px;
}

.live-matches {
  width: 25%;
}

.flag {
  width: 30px;
}

.card {
  background-color: white;
  border: none;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

.players {
  margin-top: 10px;
}

.player {
  padding: 10px 0;
  border-bottom: 1px solid #e0e0e0;
}

.player:last-child {
  border-bottom: none;
}
</style>
