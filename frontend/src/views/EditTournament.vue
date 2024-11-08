<template>
    <Navbar />
    <div v-if="tournament" class="edit-tournament-container p-5">
        <h2>Edit Tournament: {{ tournament.name }}</h2>

        <!-- Tournament Edit Form -->
        <form @submit.prevent="updateTournament" class="edit-form p-4">
            <div class="mb-3">
                <label for="type" class="form-label">Type</label>
                <select v-model="formData.type" id="type" class="form-control" required>
                    <option value="clay">Clay</option>
                    <option value="hard">Hard</option>
                    <option value="grass">Grass</option>
                </select>
            </div>

            <div class="mb-3">
                <label for="date" class="form-label">Date</label>
                <input type="date" v-model="formData.date" id="date" class="form-control" required />
            </div>

            <div class="mb-3">
                <label for="numPlayers" class="form-label">Number of Players</label>
                <select v-model="formData.numPlayers" id="numPlayers" class="form-control" required>
                    <option value="16">16</option>
                    <option value="32">32</option>
                </select>
            </div>

            <div class="mb-3">
                <label for="country" class="form-label">Country</label>
                <input type="text" v-model="formData.country" id="country" class="form-control" required />
            </div>

            <div class="mb-3">
                <label for="venue" class="form-label">Venue</label>
                <input type="text" v-model="formData.venue" id="venue" class="form-control" required />
            </div>

            <button type="submit" class="btn btn-primary">Update Tournament</button>
        </form>
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
            tournament: null,
            formData: {
                type: '',
                date: '',
                numPlayers: '',
                country: '',
                venue: ''
            }
        };
    },
    methods: {
        async fetchTournamentDetails() {
            const tournamentId = this.$route.params.id;
            try {
                const response = await axios.get(`http://localhost:8080/tournament/${tournamentId}`);
                this.tournament = response.data;

                // Populate formData with existing tournament details
                this.formData.type = this.tournament.type;
                this.formData.date = this.tournament.date;
                this.formData.numPlayers = this.tournament.numPlayers;
                this.formData.country = this.tournament.country;
                this.formData.venue = this.tournament.venue;
            } catch (error) {
                console.error("Error fetching tournament details:", error);
            }
        },
        async updateTournament() {
            const tournamentId = this.$route.params.id;
            try {
                await axios.put(`http://localhost:8080/tournament/${tournamentId}`, this.formData);
                console.log("Tournament updated successfully.");
                this.$router.push(`/tournament/${tournamentId}`);
            } catch (error) {
                console.error("Error updating tournament:", error);
            }
        }
    },
    mounted() {
        this.fetchTournamentDetails(); // Fetch tournament details on component mount
    }
};
</script>

<style scoped>
.edit-tournament-container {
    background-color: #f3eeea;
    padding: 30px;
}

h2 {
    font-family: 'Arial', sans-serif;
    color: #776b5d;
}

.edit-form {
    background-color: #fff;
    border-radius: 8px;
    padding: 20px;
    box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

.loading-message {
    font-size: 18px;
    color: #776b5d;
    text-align: center;
    padding: 20px;
}

.btn-primary {
    background-color: #b0a695;
    border-color: #b0a695;
    color: white;
}

.btn-primary:hover {
    background-color: #776b5d;
}
</style>
