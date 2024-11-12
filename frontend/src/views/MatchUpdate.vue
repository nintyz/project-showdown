<template>
    <Navbar />
    <div class="match-update" v-if="tournament">
        <h2>{{ tournament.name }} - Match Update</h2>

        <!-- Loading Screen -->
        <div v-if="loading" class="loading-screen">
            <div class="loading-content">
                <div class="spinner"></div>
                <p>Notifying players of updated match schedule...</p>
            </div>
        </div>

        <!-- Notification Popup -->
        <div v-if="notification.message" :class="['notification', notification.type]">
            {{ notification.message }}
        </div>

        <!-- Round Tabs -->
        <div class="tabs">
            <button v-for="index in 4" :key="index" @click="selectRound(index - 1)" 
                    :class="{ active: selectedRoundIndex === index - 1 }" class="tab-button">
                Round {{ index }}
            </button>
        </div>

        <!-- Match Details for Selected Round -->
        <div class="matches">
            <div v-if="selectedRound.matches && selectedRound.matches.length" class="round-section">
                <h3>Round {{ selectedRoundIndex + 1 }}</h3>
                <div v-for="(match, matchIndex) in selectedRound.matches" :key="match.id" class="match-card">
                    <h4>Match {{ matchIndex + 1 }}</h4>
                    <p><strong>Player 1:</strong> {{ match.player1 ? match.player1.name : 'Unknown' }}</p>
                    <p><strong>Player 2:</strong> {{ match.player2 ? match.player2.name : 'Unknown' }}</p>

                    <div class="date-time">
                        <label>Date & Time:</label>
                        <input
                            v-model="match.dateTime"
                            type="datetime-local"
                            placeholder="Schedule Date & Time"
                            :class="{ 'configured-input': match.dateTime && match.dateTime !== 'TBC' }"
                        />
                    </div>

                    <div class="scores">
                        <label>Player 1 Score:</label>
                        <input
                            v-model="match.player1Score"
                            type="number"
                            :class="{ 'configured-input': match.player1Score !== 0 || match.player2Score !== 0 }"
                        />
                        <label>Player 2 Score:</label>
                        <input
                            v-model="match.player2Score"
                            type="number"
                            :class="{ 'configured-input': match.player1Score !== 0 || match.player2Score !== 0 }"
                        />
                    </div>

                    <div class="action-buttons">
                        <button @click="updateDateTime(match)" class="btn update-datetime">Update Date/Time</button>
                        <button @click="updateScores(match)" class="btn update-scores">Update Scores</button>
                    </div>
                </div>
            </div>
            <div v-else class="no-matches">
                <p>This round has not started yet.</p>
            </div>
        </div>
    </div>
    <div v-else class="loading-message">Loading tournament details...</div>
</template>

<script>
import axios from 'axios';
import Navbar from '@/components/NavbarComponent.vue';

export default {
    components: { Navbar },
    data() {
        return {
            tournament: null,
            rounds: [],
            selectedRoundIndex: 0,
            notification: {
                message: '',
                type: ''
            },
            loading: false // Loading state
        };
    },
    computed: {
        selectedRound() {
            return this.rounds[this.selectedRoundIndex] || { matches: [] };
        }
    },
    methods: {
        showNotification(message, type = 'success') {
            this.notification.message = message;
            this.notification.type = type;
            setTimeout(() => { this.notification.message = ''; }, 3000);
        },
        selectRound(index) {
            this.selectedRoundIndex = index;
        },
        async fetchTournamentDetails() {
            const tournamentId = this.$route.params.id;
            if (!tournamentId) {
                console.error("Tournament ID is undefined");
                return;
            }
            try {
                const response = await axios.get(`http://localhost:8080/tournament/${tournamentId}`);
                this.tournament = response.data;
                this.rounds = response.data.rounds || [];
            } catch (error) {
                console.error('Error fetching tournament details:', error);
            }
        },
        async updateDateTime(match) {
            this.loading = true;
            try {
                await axios.put(`http://localhost:8080/match/${match.id}`, { dateTime: match.dateTime });
                this.showNotification('Date/Time updated successfully.', 'success');
                match.updatedDateTime = true;
            } catch (error) {
                console.error('Error updating date/time:', error);
                this.showNotification('Failed to update the date/time.', 'error');
            } finally {
                this.loading = false;
            }
        },
        async updateScores(match) {
            this.loading = true;
            try {
                await axios.put(`http://localhost:8080/match/${match.id}`, {
                    player1Score: match.player1Score,
                    player2Score: match.player2Score
                });
                this.showNotification('Scores updated successfully.', 'success');
                match.updatedScore = true;
            } catch (error) {
                console.error('Error updating scores:', error);
                this.showNotification('Failed to update the scores.', 'error');
            } finally {
                this.loading = false;
            }
        }
    },
    mounted() {
        this.fetchTournamentDetails();
    }
};
</script>

<style scoped>
body,
html {
    background-color: #f3eeea;
    font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
    color: #444;
}

.match-update {
    padding: 40px;
    max-width: 1200px;
    margin: 0 auto;
}

h2 {
    color: #5a5a5a;
    font-size: 2rem;
    margin-bottom: 30px;
    text-align: center;
    font-weight: 600;
}

.round-section,
.no-matches {
    margin-bottom: 40px;
}

h3 {
    font-size: 1.5rem;
    color: #333;
    margin-bottom: 20px;
    text-align: left;
}

.match-card {
    background-color: #ffffff;
    border-radius: 8px;
    padding: 25px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
    margin-bottom: 20px;
    transition: transform 0.2s, box-shadow 0.2s;
}

.match-card:hover {
    transform: translateY(-5px);
    box-shadow: 0 6px 20px rgba(0, 0, 0, 0.1);
}

.date-time,
.scores {
    display: flex;
    flex-direction: column;
    margin-bottom: 15px;
}

input[type="datetime-local"],
input[type="number"] {
    padding: 10px 12px;
    border: 1px solid #ccc;
    border-radius: 5px;
    margin-bottom: 10px;
    font-size: 1rem;
    color: #333;
}

.configured-input {
    background-color: #f2e9d8;
    border-color: #776b5d;
}

.notification {
    position: fixed;
    top: 80px;
    right: 30px;
    padding: 15px 25px;
    color: white;
    font-weight: bold;
    border-radius: 8px;
    z-index: 1000;
    animation: fade-in-out 3s ease;
    font-size: 1rem;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.notification.success {
    background-color: #28a745;
}

.notification.error {
    background-color: #dc3545;
}

.tabs {
    display: flex;
    justify-content: center;
    gap: 10px;
    margin-bottom: 20px;
}

.tab-button {
    padding: 10px 20px;
    font-weight: 600;
    border: none;
    border-radius: 5px;
    cursor: pointer;
    transition: background-color 0.3s;
}

.tab-button.active {
    background-color: #776b5d;
    color: white;
}

.tab-button:hover {
    background-color: #d3c7b4;
}

.action-buttons {
    display: flex;
    gap: 10px;
    justify-content: center;
    margin-top: 10px;
}

.btn {
    background-color: #776b5d;
    color: white;
    padding: 10px 16px;
    border-radius: 30px;
    cursor: pointer;
    transition: background-color 0.3s ease;
}

.btn.update-datetime {
    background-color: #776b5d;
}

.btn.update-scores {
    background-color: #d07d7d;
}

.loading-screen {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(255, 255, 255, 0.9);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 1001;
}

.loading-content {
    text-align: center;
}

.spinner {
    width: 50px;
    height: 50px;
    border: 5px solid #ddd;
    border-top-color: #776b5d;
    border-radius: 50%;
    animation: spin 1s linear infinite;
    margin-bottom: 1rem;
}

@keyframes spin {
    0% {
        transform: rotate(0deg);
    }
    100% {
        transform: rotate(360deg);
    }
}
</style>
