<template>
    <Navbar />
    <div class="match-update">
        <h2>{{ tournament.name }} - Match Update</h2>

        <!-- Notification Popup -->
        <div v-if="notification.message" :class="['notification', notification.type]">
            {{ notification.message }}
        </div>

        <div v-if="currentRoundMatches.length" class="matches">
            <div v-for="(match, index) in currentRoundMatches" :key="match.id" class="match-card">
                <h3>Match {{ index + 1 }}</h3>
                <p><strong>Player 1:</strong> {{ match.player1.name }}</p>
                <p><strong>Player 2:</strong> {{ match.player2.name }}</p>

                <div class="date-time">
                    <label>Date & Time (format: yyyy-MM-ddTHH:mm):</label>
                    <input v-model="match.dateTime" placeholder="TBC" type="datetime-local" />
                </div>

                <div class="scores">
                    <label>Player 1 Score:</label>
                    <input v-model="match.player1Score" type="number" />
                    <label>Player 2 Score:</label>
                    <input v-model="match.player2Score" type="number" />
                </div>

                <button @click="updateMatch(match)" class="btn btn-success">Update Match</button>
            </div>
        </div>
        <div v-else>
            <p>No matches available for the current round.</p>
        </div>
    </div>
</template>

<script>
import axios from 'axios';
import Navbar from '@/components/NavbarComponent.vue';

export default {
    components: { Navbar },
    data() {
        return {
            tournament: null,
            currentRoundMatches: [],
            notification: {
                message: '',
                type: ''
            }
        };
    },
    methods: {
        showNotification(message, type = 'success') {
            this.notification.message = message;
            this.notification.type = type;
            setTimeout(() => { this.notification.message = ''; }, 3000);
        },
        async fetchTournamentDetails() {
            const tournamentId = this.$route.params.tournamentId;
            try {
                const response = await axios.get(`http://localhost:8080/tournament/${tournamentId}`);
                this.tournament = response.data;
                this.currentRoundMatches = await this.fetchCurrentRoundMatches(this.tournament.rounds);
            } catch (error) {
                console.error('Error fetching tournament details:', error);
            }
        },
        async fetchCurrentRoundMatches(rounds) {
            const lastRound = rounds[rounds.length - 1];
            const matchRequests = lastRound.matches.map(id =>
                axios.get(`http://localhost:8080/match/${id}`).then(response => response.data)
            );
            return Promise.all(matchRequests);
        },
        async updateMatch(match) {
            const { dateTime, player1Score, player2Score } = match;
            const matchData = { dateTime, player1Score, player2Score };

            try {
                const response = await axios.put(`http://localhost:8080/match/${match.id}`, matchData);
                this.showNotification('Match updated successfully.', 'success');
                if (response.data.includes('Round Completed')) {
                    this.showNotification('Round completed! Next round generated.', 'success');
                    await this.fetchTournamentDetails();
                }
            } catch (error) {
                console.error('Error updating match:', error);
                this.showNotification('Failed to update the match.', 'error');
            }
        }
    },
    mounted() {
        this.fetchTournamentDetails();
    }
};
</script>

<style scoped>
.match-update {
    padding: 30px;
}

.match-card {
    background-color: #fff;
    border-radius: 8px;
    padding: 20px;
    box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
    margin-bottom: 20px;
}

.date-time,
.scores {
    display: flex;
    flex-direction: column;
    margin-bottom: 10px;
}

.notification {
    position: fixed;
    top: 20px;
    left: 50%;
    transform: translateX(-50%);
    padding: 15px 20px;
    color: white;
    font-weight: bold;
    border-radius: 5px;
    z-index: 1000;
    animation: fade-in-out 3s ease;
}

.notification.success {
    background-color: #4caf50;
}

.notification.error {
    background-color: #f44336;
}

@keyframes fade-in-out {
    0% {
        opacity: 0;
        transform: translateY(-10px);
    }

    10%,
    90% {
        opacity: 1;
        transform: translateY(0);
    }

    100% {
        opacity: 0;
        transform: translateY(-10px);
    }
}
</style>