<template>
    <div class="create-tournament-container">
        <Navbar />
        <div class="create-form">
            <h2>Create New Tournament</h2>

            <form @submit.prevent="submitForm" class="form">
                <!-- Tournament Name -->
                <div class="mb-3">
                    <label for="tournamentName" class="form-label">Tournament Name</label>
                    <input type="text" id="tournamentName" v-model="tournament.name" class="form-control"
                        placeholder="Enter tournament name" required />
                </div>

                <!-- Tournament Date -->
                <div class="mb-3">
                    <label for="date" class="form-label">Registration Deadline</label>
                    <input type="date" id="date" v-model="tournament.date" class="form-control" required />
                </div>

                <!-- Tournament Type (Dropdown) -->
                <div class="mb-3">
                    <label for="type" class="form-label">Tournament Type</label>
                    <select id="type" v-model="tournament.type" class="form-control" required>
                        <option value="clay">Clay</option>
                        <option value="raw">Raw</option>
                        <option value="hard">Hard</option>
                    </select>
                </div>

                <!-- Tournament Logo -->
                <div class="mb-3">
                    <label for="tournamentLogo" class="form-label">Tournament Logo</label>
                    <input type="file" id="tournamentLogo" @change="onFileChange" class="form-control" accept="image/*"
                        required />
                    <div v-if="tournament.logo" class="preview mt-3">
                        <h5>Logo Preview:</h5>
                        <img :src="tournament.logoPreview" alt="Tournament Logo Preview" class="img-fluid" />
                    </div>
                </div>

                <!-- Number of Players (Dropdown) -->
                <div class="mb-3">
                    <label for="numPlayers" class="form-label">Number of Players</label>
                    <select id="numPlayers" v-model="tournament.numPlayers" class="form-control" required>
                        <option value="16">16</option>
                        <option value="32">32</option>
                    </select>
                </div>

                <!-- Minimum and Maximum MMR -->
                <div class="mb-3">
                    <label for="minMMR" class="form-label">Minimum MMR</label>
                    <input type="number" id="minMMR" v-model="tournament.minMMR" class="form-control"
                        placeholder="Enter minimum MMR" min="0" step="0.1" required />
                </div>
                <div class="mb-3">
                    <label for="maxMMR" class="form-label">Maximum MMR</label>
                    <input type="number" id="maxMMR" v-model="tournament.maxMMR" class="form-control"
                        placeholder="Enter maximum MMR" min="0" step="0.1" required />
                </div>

                <!-- Country -->
                <div class="mb-3">
                    <label for="country" class="form-label">Country</label>
                    <input type="text" id="country" v-model="tournament.country" class="form-control"
                        placeholder="Enter country" required />
                </div>

                <!-- Venue -->
                <div class="mb-3">
                    <label for="venue" class="form-label">Venue</label>
                    <input type="text" id="venue" v-model="tournament.venue" class="form-control"
                        placeholder="Enter venue" required />
                </div>

                <!-- Submit Button -->
                <div class="mb-3 text-center">
                    <button type="submit" class="btn btn-primary">Create Tournament</button>
                </div>
            </form>

            <!-- Note -->
            <div class="note mt-3">
                <p>Players will receive a confirmation once the tournament is officially approved following the
                    scheduled tournament date.</p>
            </div>
        </div>
    </div>
</template>

<script>
import axios from 'axios';
import Navbar from '@/components/NavbarComponent.vue';
import '@/assets/main.css';

export default {
    components: {
        Navbar,
    },
    data() {
        return {
            tournament: {
                name: '',
                date: '',
                type: '',
                logo: null,
                logoPreview: '',
                numPlayers: 16,
                minMMR: 0,
                maxMMR: 0,
                country: '',
                venue: '',
            },
        };
    },
    methods: {
        onFileChange(e) {
            const file = e.target.files[0];
            if (file) {
                this.tournament.logo = file;
                this.tournament.logoPreview = URL.createObjectURL(file);
            }
        },
        async submitForm() {
            try {
                const formData = new FormData();
                formData.append('name', this.tournament.name);
                formData.append('date', this.tournament.date);
                formData.append('type', this.tournament.type);
                formData.append('status', "upcoming")
                formData.append('logo', this.tournament.logo);
                formData.append('numPlayers', this.tournament.numPlayers);
                formData.append('minMMR', this.tournament.minMMR);
                formData.append('maxMMR', this.tournament.maxMMR);
                formData.append('country', this.tournament.country);
                formData.append('venue', this.tournament.venue);

                const response = await axios.post('http://localhost:8080/tournaments', formData, {
                    headers: { 'Content-Type': 'multipart/form-data' }
                });
                console.log('Tournament created successfully:', response.data);
                this.$router.push('/tournaments'); // Redirect to the tournaments list after creation
            } catch (error) {
                console.error("Error creating tournament:", error);
            }
        },
    },
    created() {
        this.userId = localStorage.getItem('userId');
        console.log(this.userId);
    },
};
</script>

<style scoped>
.create-tournament-container {
    background-color: #f3eeea;
    min-height: 100vh;
}

.create-form {
    background-color: rgba(255, 255, 255, 0.736);
    padding: 40px;
    border-radius: 10px;
    max-width: 700px;
    margin: 0 auto;
    margin-top: 2em;
}

h2 {
    font-family: 'Arial', sans-serif;
    color: #776b5d;
    text-align: center;
    margin-bottom: 30px;
}

.form-label {
    font-weight: bold;
    color: #776b5d;
}

.form-control,
.form-select {
    background-color: #ebe3d5;
    border: none;
    color: #776b5d;
}

.form-control:focus,
.form-select:focus {
    outline: none;
    box-shadow: 0 0 5px rgba(0, 0, 0, 0.1);
}

.btn-primary {
    background-color: #b0a695;
    border-color: #b0a695;
    color: white;
}

.btn-primary:hover {
    background-color: #776b5d;
}

.preview img {
    max-width: 200px;
    border-radius: 10px;
}

.note p {
    font-size: 0.9em;
    color: #776b5d;
    text-align: center;
}
</style>
