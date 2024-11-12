<template>
    <div class="tournament-card row mb-3 p-3">
        <div class="col-md-2 d-flex align-items-center">
            <img :src="tournament.logoUrl || defaultLogo" alt="Tournament Logo" class="img-fluid tournament-logo" />
        </div>
        <div class="col-md-6 d-flex flex-column justify-content-center">
            <h4>{{ tournament.name }}</h4>
            <p>{{ tournament.location }} | {{ tournament.date }}</p>
        </div>
        <div class="col-md-2 d-flex align-items-center">
            <span :class="getStatusClass(tournament.status)">{{ tournament.status }}</span>
        </div>
        <div class="col-md-2 d-flex justify-content-end align-items-center">
            <button v-if="showViewButton" class="btn btn-outline-primary me-2" @click="viewTournament">View</button>
            <button v-if="showEditButton" class="btn btn-outline-secondary me-2" @click="editTournament">Edit</button>
            <button v-if="showDeleteButton" class="btn btn-icon" @click="confirmDeleteTournament">
                <img src="@/assets/remove.png" alt="Delete Icon" />
            </button>
            <button v-if="showRegisterButton" class="btn btn-outline-primary" @click="toggleRegistration">
                {{ isRegistered ? 'Unregister' : 'Register' }}
            </button>
            <button v-if="showViewBracketButton" :disabled="tournament.rounds.length === 0"
                class="btn btn-outline-primary" @click="viewBrackets">View Brackets</button>
        </div>
        <!-- Confirm Delete Modal -->
        <div v-if="showDeleteModal" class="modal-backdrop">
            <div class="modal-content">
                <h5>Confirm Deletion</h5>
                <p>Are you sure you want to delete this tournament?</p>
                <div class="modal-actions">
                    <button class="btn btn-secondary" @click="closeDeleteModal">Cancel</button>
                    <button class="btn btn-danger" @click="deleteTournament">Delete</button>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import axios from 'axios';

export default {
    props: {
        tournament: Object,
        role: String, // "admin", "organizer", or "player"
        userId: String, // current user's ID
    },
    data() {
        return {
            isRegistered: false,
            showDeleteModal: false,
            defaultLogo: 'https://via.placeholder.com/150',
        };
    },
    computed: {
        showViewButton() {
            return true;
        },
        showEditButton() {
            return this.role === 'admin' || (this.role === 'organizer' && this.tournament.organizerId === this.userId);
        },
        showDeleteButton() {
            return this.role === 'admin';
        },
        showRegisterButton() {
            return this.role === 'player';
        },
        showViewBracketButton() {
            return this.role === 'player' || (this.role === 'organizer' && this.tournament.organizerId === this.userId);
        },
    },
    mounted() {
        if (this.role === 'player') {
            this.checkRegistration();
        }
    },
    methods: {
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
        viewTournament() {
            this.$router.push(`/tournament/${this.tournament.id}`);
        },
        editTournament() {
            this.$router.push(`/tournament/${this.tournament.id}/edit`);
        },
        confirmDeleteTournament() {
            this.showDeleteModal = true;
        },
        closeDeleteModal() {
            this.showDeleteModal = false;
        },
        async deleteTournament() {
            try {
                await axios.delete(`http://localhost:8080/tournament/${this.tournament.id}`);
                this.$emit('tournamentDeleted', this.tournament.id);
                this.showDeleteModal = false;
            } catch (error) {
                console.error('Error deleting tournament:', error);
            }
        },
        async checkRegistration() {
            // Check if user is registered
            const response = await axios.get(`http://localhost:8080/tournament/${this.tournament.id}/isRegistered/${this.userId}`);
            this.isRegistered = response.data.isRegistered;
        },
        async toggleRegistration() {
            try {
                if (this.isRegistered) {
                    await axios.put(`http://localhost:8080/tournament/${this.tournament.id}/cancelRegistration/${this.userId}`);
                } else {
                    await axios.put(`http://localhost:8080/tournament/${this.tournament.id}/register/${this.userId}`);
                }
                this.isRegistered = !this.isRegistered;
            } catch (error) {
                console.error('Error toggling registration:', error);
            }
        },
        viewBrackets() {
            this.$router.push(`/tournament/${this.tournament.id}/bracket`);
        },
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
