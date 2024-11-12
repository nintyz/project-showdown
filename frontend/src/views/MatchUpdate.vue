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
        <!-- Completion Popup for Last Match Update -->
        <div v-if="roundCompletedNotification" class="completion-popup">
            <div class="popup-content">
                <p>All matches in this round have been completed. We are processing the matchmaking for the next round in the backend. Please allow about one minute for updates to appear on the website.</p>
                <button @click="roundCompletedNotification = false" class="btn-close">Close</button>
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
        <div v-if="selectedRound && selectedRound.matches && selectedRound.matches.length" class="matches">
            <div class="round-section">
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

                    <div class="scores" :class="{ 'highlight-scores': match.player1Score > 0 || match.player2Score > 0 }">
                        <label>Player 1 Score:</label>
                        <input
                            v-model="match.player1Score"
                            type="number"
                            :class="{ 'configured-input': match.player1Score > 0 }"
                        />
                        <label>Player 2 Score:</label>
                        <input
                            v-model="match.player2Score"
                            type="number"
                            :class="{ 'configured-input': match.player2Score > 0 }"
                        />
                    </div>

                    <div class="action-buttons">
                        <button @click="updateDateTime(match)" class="btn update-datetime">
                            <i class="icon-calendar"></i>Update Date/Time
                        </button>
                        <button @click="updateScores(match)" class="btn update-scores">
                            <i class="icon-trophy"></i>Update Scores
                        </button>
                    </div>
                </div>
            </div>
        </div>
        
        <div v-else class="not-started-message">
            <p>Round {{ selectedRoundIndex + 1 }} has not started yet.</p>
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
            roundCompletedNotification: false,
            loading:false
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
            try {
                await axios.put(`http://localhost:8080/match/${match.id}`, {
                    player1Score: match.player1Score,
                    player2Score: match.player2Score
                });
                this.showNotification('Scores updated successfully.', 'success');
                match.updatedScore = true;

                if (this.isLastMatchInRound()) {
                    this.roundCompletedNotification = true;
                }
            } catch (error) {
                console.error('Error updating scores:', error);
                this.showNotification('Failed to update the scores.', 'error');
            }
        },
        isLastMatchInRound() {
            const currentRound = this.rounds[this.selectedRoundIndex];
            return currentRound && currentRound.matches.every(
                m => m.player1Score > 0 || m.player2Score > 0
            );
        },
        selectRound(index) {
            this.selectedRoundIndex = index;
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

.round-section {
    margin-bottom: 40px;
}

h3 {
    font-size: 1.5rem;
    color: #333;
    margin-bottom: 20px;
    border-bottom: 2px solid #e0e0e0;
    padding-bottom: 10px;
    text-align: left;
}

.tabs {
    display: flex;
    justify-content: center;
    margin-bottom: 20px;
}

.tab-button {
    background-color: #f3eeea;
    border: 1px solid #ccc;
    padding: 10px 20px;
    cursor: pointer;
    transition: background-color 0.3s;
    border-radius: 5px;
    margin: 0 5px;
}

.tab-button.active, .tab-button:hover {
    background-color: #776b5d;
    color: white;
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

.match-card h4 {
    font-size: 1.25rem;
    color: #444;
    margin-bottom: 15px;
    font-weight: 500;
}

.date-time,
.scores {
    display: flex;
    flex-direction: column;
    margin-bottom: 15px;
}

.scores.highlight-scores input {
    background-color: #f7f3eb;
    /* border-color: #776b5d; */
}

.date-time label,
.scores label {
    font-weight: 500;
    font-size: 1rem;
    margin-bottom: 5px;
    color: #555;
}

input[type="datetime-local"],
input[type="number"] {
    padding: 10px 12px;
    border: 1px solid #ccc;
    border-radius: 5px;
    margin-bottom: 10px;
    font-size: 1rem;
    color: #333;
    outline: none;
    transition: border-color 0.2s;
}

.configured-input {
    background-color: #f7f3eb;
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
.action-buttons {
    display: flex;
    gap: 10px;
    justify-content: center;
    margin-top: 10px;
}

.btn {
    display: flex;
    align-items: center;
    justify-content: center;
    background-color: #776b5d;
    color: white;
    border: none;
    padding: 10px 16px;
    font-size: 1rem;
    font-weight: 600;
    cursor: pointer;
    border-radius: 30px;
    transition: background-color 0.3s ease;
    margin: 0px 10px;
}

.btn .icon-calendar,
.btn .icon-trophy {
    font-size: 1.2rem;
}

.update-datetime {
    background-color: #776b5d;
    width: 13em;
}

.update-datetime:hover {
    background-color: #776b5d92;
    color: white;
}

.update-scores {
    background-color: #d07d7d;
    width: 13em;
}

.update-scores:hover {
    background-color: #d07d7da5;
    color: white;
}

.completion-popup {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, 0.6);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 2000;
}

.overlay {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, 0.5);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 2000;
}

.overlay-content {
    background-color: #ffffff;
    padding: 20px 30px;
    border-radius: 8px;
    text-align: center;
    color: #333;
    max-width: 500px;
}

.overlay-btn {
    background-color: #776b5d;
    color: white;
    border: none;
    padding: 10px 16px;
    font-size: 1rem;
    font-weight: 600;
    cursor: pointer;
    border-radius: 30px;
    margin: 10px;
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
.completion-popup {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, 0.6);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 2000;
}

.popup-content {
    background-color: #ffffff;
    padding: 20px 30px;
    border-radius: 8px;
    text-align: center;
    color: #333;
    max-width: 500px;
}

.btn-close {
    margin-top: 15px;
    background-color: #776b5d;
    color: white;
    border: none;
    padding: 10px 16px;
    font-size: 1rem;
    font-weight: 600;
    cursor: pointer;
    border-radius: 30px;
}
</style>
