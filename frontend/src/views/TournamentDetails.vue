<template>
    <Navbar />
    <div v-if="tournament" class="tournament-details p-5">
        <h2>{{ tournament.name }}</h2>

        <!-- Notification Popup -->
        <div v-if="notification.message" :class="['notification', notification.type]">
            {{ notification.message }}
        </div>

        <!-- Tournament Details Section -->
        <div class="details-card p-4 mb-4">
            <img :src="tournament.logoUrl || defaultLogo" alt="Tournament Logo" class="img-fluid tournament-logo mb-3" />
            <p><strong>Location:</strong> {{ tournament.venue }}</p>
            <p><strong>Date:</strong> {{ formattedDateTime }}</p>
            <p>
                <strong>Status:</strong>
                <span :class="getStatusClass(tournament.status)">{{ statusText }}</span>
            </p>
            <p><strong>Number of Players:</strong> {{ tournament.numPlayers }}</p>
            <p><strong>Minimum MMR:</strong> {{ tournament.minMMR }}</p>
            <p><strong>Maximum MMR:</strong> {{ tournament.maxMMR }}</p>

            <!-- Action Buttons based on role and registration status -->
            <div class="action-buttons mt-4">
                <!-- Render for Players -->
                <template v-if="role === 'player'">
                    <button v-if="!isUserRegistered" class="btn btn-outline-primary me-2"
                        @click="registerForTournament">
                        Register
                    </button>
                    <button v-else class="btn btn-outline-secondary me-2" @click="unregisterFromTournament">
                        Unregister
                    </button>
                    <button :disabled="bracketButtonDisabled" :class="{ 'btn-disabled': bracketButtonDisabled }"
                        class="btn btn-outline-secondary me-2" @click="viewBrackets">
                        {{ bracketButtonText }}
                    </button>
                </template>

                <!-- Render for Organizers -->
                <template v-else-if="role === 'organizer'">
                    <button class="btn btn-outline-primary me-2" @click="editTournament">Edit</button>
                    <button class="btn btn-outline-danger me-2" @click="cancelTournament">Cancel</button>
                    <button :disabled="bracketButtonDisabled" :class="{ 'btn-disabled': bracketButtonDisabled }"
                        class="btn btn-outline-secondary me-2" @click="viewBrackets">
                        {{ bracketButtonText }}
                    </button>
                    <button class="btn btn-outline-success me-2" @click="progressTournament">
                        Progress Tournament
                    </button>
                </template>

                <!-- For other roles or non-specific access -->
                <template v-else>
                    <button :disabled="bracketButtonDisabled" :class="{ 'btn-disabled': bracketButtonDisabled }"
                        class="btn btn-outline-secondary me-2" @click="viewBrackets">
                        {{ bracketButtonText }}
                    </button>
                </template>
            </div>
        </div>

        <!-- Tournament Players -->
        <div v-if="sortedPlayers.length">
            <h3>Players Registered (A-Z)</h3>
            <div v-for="(player, index) in sortedPlayers" :key="player.id" class="player-info">
                <h6>{{ index + 1 }}. {{ player.name }} || {{ player.playerDetails.country || 'Country not specified' }}
                </h6>
            </div>
        </div>
        <div v-else>
            <p>No players are currently registered for this tournament.</p>
        </div>
    </div>

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
            tournament: null,
            userId: 'n7T2VSiOZ2m0kuzEh9yJ', // Sample player ID
            defaultLogo: 'https://via.placeholder.com/150',
            // role: localStorage.getItem('role') , // Retrieve role from local storage
            role: 'player', // Retrieve role from local storage
            users: [], // Store user data for registered players
            notification: {
                message: '',
                type: '' // 'success' or 'error'
            },
        };
    },
    computed: {
        isUserRegistered() {
            return this.tournament?.users?.includes(this.userId);
        },
        sortedPlayers() {
            // Sort players by name and return with country details
            // eslint-disable-next-line
            return this.users.sort((a, b) => a.name.localeCompare(b.name));
        },
        bracketButtonText() {
            return this.tournament?.rounds?.length ? 'View Brackets' : 'Brackets Not Available Yet';
        },
        bracketButtonDisabled() {
            return !this.tournament || !this.tournament.rounds?.length;
        },
        formattedDateTime() {
            return new Date(this.tournament.dateTime).toLocaleString(undefined, { year: 'numeric', month: 'long', day: 'numeric', hour: '2-digit', minute: '2-digit' });
        },
        statusText() {
            return this.tournament.status === 'Registration' ? 'Open for Registration' : this.tournament.status;
        },
    },
    methods: {
        showNotification(message, type = 'success') {
            this.notification.message = message;
            this.notification.type = type;

            // Hide the notification after 3 seconds
            setTimeout(() => {
                this.notification.message = '';
            }, 3000);
        },
        async fetchTournamentDetails() {
            const tournamentId = this.$route.params.id;
            try {
                const response = await axios.get(`http://localhost:8080/tournament/${tournamentId}`);
                this.tournament = response.data;

                // Fetch details of users registered in the tournament
                if (this.tournament.users?.length) {
                    const userPromises = this.tournament.users.map(userId => axios.get(`http://localhost:8080/user/${userId}`));
                    const userResponses = await Promise.all(userPromises);
                    this.users = userResponses.map(response => response.data);
                }
            } catch (error) {
                console.error('Error fetching tournament details:', error);
            }
        },
        getStatusClass(status) {
            return { Upcoming: 'text-warning', Ongoing: 'text-success', Ended: 'text-danger' }[status] || 'text-muted';
        },
        editTournament() {
            this.$router.push(`/tournament/${this.$route.params.id}/edit`);
        },
        async cancelTournament() {
            if (confirm('Are you sure you want to cancel this tournament?')) {
                try {
                    await axios.put(`http://localhost:8080/tournament/${this.$route.params.id}/${this.userId}`, { status: 'Cancelled' });
                    this.showNotification('Tournament cancelled successfully.', 'success');
                    this.$router.push('/tournaments');
                } catch (error) {
                    console.error('Error cancelling tournament:', error);
                    this.showNotification('Failed to cancel the tournament.', 'error');
                }
            }
        },
        async registerForTournament() {
            try {
                 // eslint-disable-next-line 
                const response = await axios.put(
                    `http://localhost:8080/tournament/${this.tournament.id}/register/${this.userId}`
                );
                this.showNotification('Successfully registered for the tournament.', 'success');
                await this.fetchTournamentDetails();
            } catch (error) {
                console.error('Error registering for tournament:', error);
                this.showNotification('Failed to register for the tournament.', 'error');
            }
        },
        async unregisterFromTournament() {
            try {
                 // eslint-disable-next-line 
                const response = await axios.put(
                    `http://localhost:8080/tournament/${this.tournament.id}/cancelRegistration/${this.userId}`
                );
                this.showNotification('Successfully unregistered from the tournament.', 'success');
                await this.fetchTournamentDetails();
                // Remove user from the users array after successful unregistration
                this.users = this.users.filter((user) => user.id !== this.userId);
            } catch (error) {
                console.error('Error unregistering from tournament:', error);
                this.showNotification('Failed to unregister from the tournament.', 'error');
            }
        },
        async progressTournament() {
            try {
                await axios.put(`http://localhost:8080/tournament/${this.$route.params.id}/matches`);
                this.showNotification('Tournament progressed successfully.', 'success');
                await this.fetchTournamentDetails();
            } catch (error) {
                console.error('Error progressing tournament:', error);
                this.showNotification('Failed to progress the tournament.', 'error');
            }
        },
        viewBrackets() {
            this.$router.push(`/tournament/${this.$route.params.id}/dashboard`);
        },
    },
    mounted() {
        this.fetchTournamentDetails();
    },
};
</script>

<style scoped>
body,
html {
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

.notification {
    position: fixed;
    top: 40px;
    right: 4%;
    transform: translateX(4%);
    padding: 15px 20px;
    color: white;
    font-weight: bold;
    border-radius: 5px;
    animation: fade-in-out 3s ease;
    z-index: 1000;
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
    10%, 90% {
        opacity: 1;
        transform: translateY(0);
    }
    100% {
        opacity: 0;
        transform: translateY(-10px);
    }
}

.btn-outline-primary {
    background-color: #b0a695;
    color: white;
    border: none;
}

.btn-outline-primary:hover {
    background-color: #776b5d;
}

.btn-outline-danger {
    background-color: #dc3545;
    color: white;
    border: none;
}

.btn-outline-danger:hover {
    background-color: #a71d2a;
}

.btn-outline-secondary {
    background-color: #6c757d;
    color: white;
    border: none;
}

.btn-outline-secondary:hover {
    background-color: #5a6268;
}

.btn-outline-success {
    background-color: #28a745;
    color: white;
    border: none;
}

.btn-outline-success:hover {
    background-color: #218838;
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

.btn-disabled {
    cursor: not-allowed;
    opacity: 0.65;
    background-color: #218838;
}
</style>
