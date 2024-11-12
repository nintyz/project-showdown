<template>
    <Navbar />
    <div v-if="tournament" class="tournament-details p-5">
        <h2>{{ tournament.name }}</h2>

        <!-- Tournament Details Section -->
        <div class="details-card p-4 mb-4">
            <img :src="tournament.logoUrl" alt="Tournament Logo" class="img-fluid tournament-logo mb-3" />
            <p><strong>Location:</strong> {{ tournament.venue }}</p>
            <p><strong>Date:</strong> {{ tournament.date }}</p>
            <p><strong>Status:</strong> <span :class="getStatusClass(tournament.status)">{{ tournament.status }}</span>
            </p>
            <p><strong>Number of Players:</strong> {{ tournament.numPlayers }}</p>
            <p><strong>Minimum MMR:</strong> {{ tournament.minMMR }}</p>
            <p><strong>Maximum MMR:</strong> {{ tournament.maxMMR }}</p>

            <!-- Action Buttons -->
            <div class="action-buttons mt-4">
                <button class="btn btn-outline-primary me-2" @click="editTournament">Edit</button>
                <button class="btn btn-outline-danger me-2" @click="deleteTournament">Delete</button>
                <button class="btn btn-outline-secondary me-2" @click="viewBrackets">View Brackets</button>
            </div>
        </div>

        <!-- Tournament Players -->
        <div v-if="sortedPlayers.length">
            <h3>Players Registered (A-Z)</h3>
            <div v-for="(player, index) in sortedPlayers" :key="player.id" class="player-info">
                <h6>{{ index + 1 }}. {{ player.name }} || {{ player.playerDetails.country }}</h6>
            </div>
        </div>
    </div>

    <!-- Loading message if tournament data is not yet loaded -->
    <div v-else class="loading-message">Loading tournament details...</div>
</template>

<script>
import axios from 'axios';
import Navbar from '@/components/NavbarComponent.vue';

export default {
    components: {
        Navbar,
    },
    data() {
        return {
            tournament: null, // Will hold tournament data once fetched
        };
    },
    computed: {
        sortedPlayers() {
            const players = [];

            // Collect all players from each match
            this.tournament.rounds?.forEach(round => {
                round.matches.forEach(match => {
                    players.push(match.player1);
                    players.push(match.player2);
                });
            });

            // Remove duplicates and sort alphabetically by player name
            const uniquePlayers = Array.from(new Set(players.map(p => p.id)))
                .map(id => players.find(p => p.id === id))
                .sort((a, b) => a.name.localeCompare(b.name)); // Use player.name directly

            return uniquePlayers;
        }
    },

    methods: {
        async fetchTournamentDetails() {
            const tournamentId = this.$route.params.id;
            try {
                const response = await axios.get(`http://localhost:8080/tournament/${tournamentId}`);
                this.tournament = response.data;
            } catch (error) {
                console.error("Error fetching tournament details:", error);
            }
        },
        getStatusClass(status) {
            switch (status) {
                case "Upcoming":
                    return "text-warning";
                case "Ongoing":
                    return "text-success";
                case "Ended":
                    return "text-danger";
                default:
                    return "text-muted";
            }
        },
        editTournament() {
            this.$router.push(`/tournament/${this.$route.params.id}/edit`);
        },
        async deleteTournament() {
            try {
                const tournamentId = this.$route.params.id;
                await axios.delete(`http://localhost:8080/tournament/${tournamentId}`);
                console.log("Tournament deleted successfully.");
                this.$router.push("/tournaments");
            } catch (error) {
                console.error("Error deleting tournament:", error);
            }
        },
        async progressTournament() {
            const tournamentId = this.$route.params.id;
            try {
                const response = await axios.put(`http://localhost:8080/tournament/${tournamentId}/matches`);
                console.log("Tournament progressed successfully:", response.data);
                await this.fetchTournamentDetails(); // Refresh details to show updated rounds and matches
            } catch (error) {
                console.error("Error progressing tournament:", error);
            }
        },
        viewBrackets() {
            this.$router.push(`/tournament/${this.$route.params.id}/dashboard`);
        },
    },
    mounted() {
        this.fetchTournamentDetails(); // Fetch tournament details when component is mounted
    },
};
</script>

<style scoped>
.body {
    background-color: #f3eeea;
}

.tournament-details {
    background-color: #f3eeea;
    padding: 30px;
}

h2 {
    font-family: 'Arial', sans-serif;
    color: #776b5d;
}

.details-card {
    background-color: #fff;
    border-radius: 8px;
    box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

.tournament-logo {
    width: 150px;
    height: auto;
}

.rounds-section h3 {
    color: #776b5d;
}

.round-card {
    background-color: #fff;
    border-radius: 8px;
    box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

.match-card {
    background-color: #ebe3d5;
    border-radius: 5px;
    padding: 15px;
}

.player-info {
    background-color: #f9f9f9;
    border-radius: 5px;
    padding: 10px;
    margin: 10px 0;
}

.loading-message {
    font-size: 18px;
    color: #776b5d;
    text-align: center;
    padding: 20px;
}

.action-buttons .btn-outline-primary {
    background-color: #b0a695;
    color: white;
    border: none;
}

.action-buttons .btn-outline-primary:hover {
    background-color: #776b5d;
}

.action-buttons .btn-outline-danger {
    background-color: #dc3545;
    color: white;
    border: none;
}

.action-buttons .btn-outline-danger:hover {
    background-color: #a71d2a;
}

.action-buttons .btn-outline-secondary {
    background-color: #6c757d;
    color: white;
    border: none;
}

.action-buttons .btn-outline-secondary:hover {
    background-color: #5a6268;
}

.text-warning {
    color: #FFA500;
}

.text-success {
    color: #28a745;
}

.text-danger {
    color: #dc3545;
}
</style>
