<template>
  <div class="dashboard-container">
    <!-- Navbar -->
    <Navbar />

    <!-- Tournament Details Section -->
    <div class="tournament-details container my-4 text-center">
      <img :src="tournament.logo" alt="Tournament Logo" class="tournament-logo mb-3" />
      <h2>{{ tournament.name }}</h2>
      <p>{{ tournament.venue }} | {{ tournament.date }}</p>
      <span class="badge rounded-pill status-badge">{{ tournament.status }}</span>
    </div>

    <div class="content container-fluid mx-5">
      <div class="row">
        <!-- Tournament Bracket -->
        <div class="col-12">
          <h4>Current Tournament Bracket</h4>
          <bracket :rounds="rounds">
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
import Navbar from '@/components/NavbarComponent.vue';
import Bracket from "./Bracket";
import '@/assets/main.css';

const rounds = [
    // Round of 32
    {
        games: [
            { player1: { id: "1", name: "Alex", winner: true, scores: [6, 6] }, player2: { id: "2", name: "John", winner: false, scores: [3, 4] } },
            { player1: { id: "3", name: "Mark", winner: false, scores: [4, 3] }, player2: { id: "4", name: "Leo", winner: true, scores: [6, 6] } },
            { player1: { id: "5", name: "Nick", winner: true, scores: [6, 7] }, player2: { id: "6", name: "Paul", winner: false, scores: [4, 5] } },
            { player1: { id: "7", name: "Jake", winner: false, scores: [4, 2] }, player2: { id: "8", name: "Max", winner: true, scores: [6, 6] } },
            { player1: { id: "9", name: "Tom", winner: true, scores: [7, 6] }, player2: { id: "10", name: "Eli", winner: false, scores: [5, 4] } },
            { player1: { id: "11", name: "Sam", winner: false, scores: [3, 2] }, player2: { id: "12", name: "Jack", winner: true, scores: [6, 6] } },
            { player1: { id: "13", name: "Rob", winner: true, scores: [6, 7] }, player2: { id: "14", name: "Ben", winner: false, scores: [4, 6] } },
            { player1: { id: "15", name: "Dan", winner: false, scores: [5, 6] }, player2: { id: "16", name: "Ray", winner: true, scores: [6, 7] } },
            { player1: { id: "17", name: "Chris", winner: true, scores: [6, 6] }, player2: { id: "18", name: "Tim", winner: false, scores: [3, 2] } },
            { player1: { id: "19", name: "James", winner: false, scores: [2, 3] }, player2: { id: "20", name: "Steve", winner: true, scores: [6, 6] } },
            { player1: { id: "21", name: "Mike", winner: true, scores: [7, 6] }, player2: { id: "22", name: "Dave", winner: false, scores: [5, 4] } },
            { player1: { id: "23", name: "George", winner: false, scores: [4, 2] }, player2: { id: "24", name: "Henry", winner: true, scores: [6, 6] } },
            { player1: { id: "25", name: "Fred", winner: true, scores: [6, 6] }, player2: { id: "26", name: "Ryan", winner: false, scores: [3, 4] } },
            { player1: { id: "27", name: "Evan", winner: false, scores: [5, 2] }, player2: { id: "28", name: "Nate", winner: true, scores: [7, 6] } },
            { player1: { id: "29", name: "Harry", winner: true, scores: [6, 6] }, player2: { id: "30", name: "Phil", winner: false, scores: [4, 3] } },
            { player1: { id: "31", name: "Kyle", winner: false, scores: [3, 2] }, player2: { id: "32", name: "Zane", winner: true, scores: [6, 6] } }
        ]
    },
    // Round of 16
    {
        games: [
            { player1: { id: "1", name: "Alex", winner: true, scores: [6, 6] }, player2: { id: "4", name: "Leo", winner: false, scores: [4, 5] } },
            { player1: { id: "5", name: "Nick", winner: true, scores: [7, 6] }, player2: { id: "8", name: "Max", winner: false, scores: [5, 4] } },
            { player1: { id: "9", name: "Tom", winner: true, scores: [7, 6] }, player2: { id: "12", name: "Jack", winner: false, scores: [5, 4] } },
            { player1: { id: "13", name: "Rob", winner: true, scores: [6, 7] }, player2: { id: "16", name: "Ray", winner: false, scores: [4, 6] } },
            { player1: { id: "17", name: "Chris", winner: false, scores: [3, 5] }, player2: { id: "20", name: "Steve", winner: true, scores: [6, 6] } },
            { player1: { id: "21", name: "Mike", winner: true, scores: [6, 6] }, player2: { id: "24", name: "Henry", winner: false, scores: [3, 2] } },
            { player1: { id: "25", name: "Fred", winner: false, scores: [5, 4] }, player2: { id: "28", name: "Nate", winner: true, scores: [7, 7] } },
            { player1: { id: "29", name: "Harry", winner: false, scores: [4, 3] }, player2: { id: "32", name: "Zane", winner: true, scores: [6, 6] } }
        ]
    },
    // Quarterfinals
    {
        games: [
            { player1: { id: "1", name: "Alex", winner: true, scores: [6, 6] }, player2: { id: "5", name: "Nick", winner: false, scores: [4, 5] } },
            { player1: { id: "9", name: "Tom", winner: true, scores: [7, 6] }, player2: { id: "13", name: "Rob", winner: false, scores: [5, 3] } },
            { player1: { id: "20", name: "Steve", winner: false, scores: [3, 2] }, player2: { id: "21", name: "Mike", winner: true, scores: [6, 6] } },
            { player1: { id: "28", name: "Nate", winner: false, scores: [4, 2] }, player2: { id: "32", name: "Zane", winner: true, scores: [6, 6] } }
        ]
    },
    // Semifinals
    {
        games: [
            { player1: { id: "1", name: "Alex", winner: true, scores: [6, 6] }, player2: { id: "9", name: "Tom", winner: false, scores: [3, 4] } },
            { player1: { id: "21", name: "Mike", winner: false, scores: [4, 3] }, player2: { id: "32", name: "Zane", winner: true, scores: [6, 6] } }
        ]
    },
    // Final
    {
        games: [
            { player1: { id: "1", name: "Alex", winner: false, scores: [4, 5] }, player2: { id: "32", name: "Zane", winner: true, scores: [6, 7] } }
        ]
    }
];


export default {
  components: {
    Bracket,
    Navbar,
  },
  data() {
    return {
      rounds: rounds,
      tournament: {
        name: "ATP Finals",
        venue: "Turin, Italy",
        date: "10 - 17 September 2023",
        status: "Ongoing",
        logo: "https://cdn.cookielaw.org/logos/d650bf03-392a-4f58-9e0f-30e4e5889bc1/4166a46d-b503-4394-9a8d-cfc6856bb183/d11831c3-5497-41f0-bcc8-a7d664ee017f/nitto.png"
      }
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

.player {
  padding: 10px 0;
  border-bottom: 1px solid #e0e0e0;
}

.player:last-child {
  border-bottom: none;
}

.flag {
  width: 30px;
}

.card {
  background-color: white;
  border: none;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}
</style>
