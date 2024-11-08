<template>
    <div class="dashboard-container">
      <!-- Navbar -->
      <Navbar />
  
      <!-- Tournament Details Section -->
      <div v-if="tournament" class="tournament-details container my-4 text-center">
        <img :src="tournament.logoUrl || defaultLogo" alt="Tournament Logo" class="tournament-logo mb-3" />
        <h2>{{ tournament.name }}</h2>
        <p>{{ tournament.venue }} | {{ tournament.date }}</p>
        <span class="badge rounded-pill status-badge">{{ tournament.status }}</span>
      </div>
  
      <div v-else class="loading-message">
        Loading tournament details...
      </div>
  
      <div v-if="tournament" class="content container-fluid mx-5">
        <div class="row justify-content-center">
          <!-- Centered Tournament Bracket Section -->
          <div class="col-lg-8 col-md-10 bracket-wrapper">
            <h4 class="text-center">Current Tournament Bracket</h4>
            <bracket :rounds="formattedRounds">
              <template #player="{ player }">
                {{ player.name }}
              </template>
            </bracket>
          </div>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  import axios from 'axios';
  import Navbar from '@/components/NavbarComponent.vue';
  import Bracket from './Bracket.vue';
  
  export default {
    components: {
      Navbar,
      Bracket,
    },
    data() {
      return {
        tournament: null,
        rounds: [], // Store fetched rounds data here
        formattedRounds: [], // For displaying formatted data in the Bracket component
        defaultLogo: 'https://via.placeholder.com/150', // Default logo if none provided
      };
    },
    async created() {
      await this.fetchTournamentData();
      this.formatRoundsData();
    },
    methods: {
      async fetchTournamentData() {
        const tournamentId = this.$route.params.id;
        try {
          const response = await axios.get(`http://localhost:8080/tournament/${tournamentId}`);
          this.tournament = response.data;
  
          // Populate rounds data from tournament data
          this.rounds = this.tournament.rounds;
        } catch (error) {
          console.error('Error fetching tournament details:', error);
        }
      },
      formatRoundsData() {
        this.formattedRounds = this.rounds.map(round => ({
          games: round.matches.map(match => ({
            player1: {
              id: match.player1Id,
              name: match.player1.playerDetails.name,
              winner: match.player1Score > match.player2Score,
              scores: [match.player1Score],
            },
            player2: {
              id: match.player2Id,
              name: match.player2.playerDetails.name,
              winner: match.player2Score > match.player1Score,
              scores: [match.player2Score],
            },
          })),
        }));
      },
    },
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
    text-align: center;
  }
  
  .tournament-logo {
    width: 80px;
    height: auto;
    margin-bottom: 10px;
  }
  
  .status-badge {
    background-color: #28a745;
    color: white;
    font-size: 14px;
    padding: 8px 12px;
  }
  
  .bracket-wrapper {
    display: flex;
    flex-direction: column;
    align-items: center;
    text-align: center;
  }
  
  .bracket-container {
    width: 100%;
    margin-top: 20px;
  }
  
  .loading-message {
    font-size: 18px;
    color: #776b5d;
    text-align: center;
    padding: 20px;
  }
  </style>
  