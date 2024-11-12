<template>
    <Navbar />
    <div v-if="tournament" class="tournament-details p-5">
        <h2>{{ tournament.name }}</h2>

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

            <!-- Action Buttons -->
            <div class="action-buttons mt-4">
                <!-- For Players -->
                <template v-if="isPlayer">
                    <button v-if="!isUserRegistered" class="btn btn-outline-primary me-2" @click="registerForTournament">
                        Register
                    </button>
                    <button v-else class="btn btn-outline-secondary me-2" @click="unregisterFromTournament">
                        Unregister
                    </button>
                    <button :disabled="bracketButtonDisabled" class="btn btn-outline-secondary me-2" @click="viewBrackets">
                        {{ bracketButtonText }}
                    </button>
                </template>

                <!-- For Organizers -->
                <template v-else-if="isOrganizer && isUserOrganizer">
                    <button class="btn btn-outline-primary me-2" @click="editTournament">Edit</button>
                    <button class="btn btn-outline-danger me-2" @click="cancelTournament">Cancel</button>
                    <button :disabled="bracketButtonDisabled" class="btn btn-outline-secondary me-2" @click="viewBrackets">
                        {{ bracketButtonText }}
                    </button>
                    <button class="btn btn-outline-success me-2" @click="progressTournament">
                        Progress Tournament
                    </button>
                </template>

                <!-- For Mutual Access -->
                <template v-else>
                    <button :disabled="bracketButtonDisabled" class="btn btn-outline-secondary me-2" @click="viewBrackets">
                        {{ bracketButtonText }}
                    </button>
                </template>
            </div>
        </div>

        <!-- Tournament Players -->
        <div v-if="sortedPlayers.length">
            <h3>Players Registered (A-Z)</h3>
            <div v-for="(player, index) in sortedPlayers" :key="player.id" class="player-info">
                <h6>{{ index + 1 }}. {{ player.name }} || {{ player.playerDetails.country }}</h6>
            </div>
        </div>
        <div v-else>
            <p>No players are currently registered for this tournament.</p>
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
            userId: 'n7T2VSiOZ2m0kuzEh9yJ', // Hardcoded player ID
            organizerId: 'anQ1ep6A96Wh5oNhidaJ', // Hardcoded organizer ID
            defaultLogo: 'https://via.placeholder.com/150', // Default logo if none provided
        };
    },
    computed: {
        isUserRegistered() {
            return this.tournament.users && this.tournament.users.includes(this.userId);
        },
        isUserOrganizer() {
            return this.tournament.organizerId === this.organizerId;
        },
        isPlayer() {
            return this.userId === 'n7T2VSiOZ2m0kuzEh9yJ';
        },
        isOrganizer() {
            return this.organizerId === 'anQ1ep6A96Wh5oNhidaJ';
        },
        sortedPlayers() {
            const players = [];

            if (this.tournament?.rounds && Array.isArray(this.tournament.rounds)) {
                this.tournament.rounds.forEach((round) => {
                    if (round.matches && Array.isArray(round.matches)) {
                        round.matches.forEach((match) => {
                            if (match.player1) players.push(match.player1);
                            if (match.player2) players.push(match.player2);
                        });
                    }
                });
            }

            const uniquePlayers = Array.from(new Set(players.map((p) => p.id)))
                .map((id) => players.find((p) => p.id === id))
                .sort((a, b) => a.name.localeCompare(b.name));

            return uniquePlayers;
        },
        bracketButtonText() {
            return this.tournament && this.tournament.rounds && this.tournament.rounds.length
                ? 'View Brackets'
                : 'Brackets Not Available Yet';
        },
        bracketButtonDisabled() {
            return !this.tournament || !this.tournament.rounds || !this.tournament.rounds.length;
        },
        formattedDateTime() {
            return new Date(this.tournament.dateTime).toLocaleString(undefined, {
                year: 'numeric',
                month: 'long',
                day: 'numeric',
                hour: '2-digit',
                minute: '2-digit',
            });
        },
        statusText() {
            return this.tournament.status === 'registration' ? 'Open for Registration' : this.tournament.status;
        },
    },
    methods: {
        async fetchTournamentDetails() {
            const tournamentId = this.$route.params.id;
            try {
                const response = await axios.get(`http://localhost:8080/tournament/${tournamentId}`);
                this.tournament = response.data;
                console.log(this.tournament);
                
            } catch (error) {
                console.error('Error fetching tournament details:', error);
            }
        },
        getStatusClass(status) {
            switch (status) {
                case 'Upcoming':
                    return 'text-warning';
                case 'Ongoing':
                    return 'text-success';
                case 'Ended':
                    return 'text-danger';
                default:
                    return 'text-muted';
            }
        },
        editTournament() {
            this.$router.push(`/tournament/${this.$route.params.id}/edit`);
        },
        async cancelTournament() {
            if (confirm('Are you sure you want to cancel this tournament?')) {
                try {
                    const tournamentId = this.$route.params.id;
                    await axios.put(`http://localhost:8080/tournament/${tournamentId}/${this.organizerId}`, {
                        status: 'Cancelled',
                    });
                    alert('Tournament cancelled successfully.');
                    this.$router.push('/tournaments');
                } catch (error) {
                    console.error('Error cancelling tournament:', error);
                    alert('Failed to cancel the tournament.');
                }
            }
        },
        async registerForTournament() {
            try {
                await axios.put(
                    `http://localhost:8080/tournament/${this.tournament.id}/register/${this.userId}`
                );
                alert('Successfully registered for the tournament.');
                await this.fetchTournamentDetails();
            } catch (error) {
                console.error('Error registering for tournament:', error);
                alert('Failed to register for the tournament.');
            }
        },
        async unregisterFromTournament() {
            try {
                await axios.put(
                    `http://localhost:8080/tournament/${this.tournament.id}/cancelRegistration/${this.userId}`
                );
                alert('Successfully unregistered from the tournament.');
                await this.fetchTournamentDetails();
            } catch (error) {
                console.error('Error unregistering from tournament:', error);
                alert('Failed to unregister from the tournament.');
            }
        },
        async progressTournament() {
            const tournamentId = this.$route.params.id;
            try {
                await axios.put(`http://localhost:8080/tournament/${tournamentId}/matches`);
                alert('Tournament progressed successfully.');
                await this.fetchTournamentDetails(); // Refresh details to show updated rounds and matches
            } catch (error) {
                console.error('Error progressing tournament:', error);
                alert('Failed to progress the tournament.');
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

.action-buttons .btn-outline-success {
    background-color: #28a745;
    color: white;
    border: none;
}

.action-buttons .btn-outline-success:hover {
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
</style>