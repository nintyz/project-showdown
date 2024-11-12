<template>
    <div class="all-players-container">

    <Navbar />
    <div class="container mt-4">
        <h1 class="text-center mb-4">All Players</h1>
        <div class="sort-container mb-3">
            <label for="sortColumn" class="sort-label">Sort by:</label>
            <select v-model="sortColumn" class="form-select" id="sortColumn">
                <option value="name">Name</option>
                <option value="country">Country</option>
                <option value="rank">Rank</option>
                <option value="elo">ELO</option>
            </select>
        </div>
        <table class="table table-striped table-hover">
            <thead class="table-header">
                <tr>
                    <th>Name</th>
                    <th>Country</th>
                    <th>Rank</th>
                    <th>ELO</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="player in sortedPlayers" :key="player.id">
                    <td>{{ player.name || 'N/A' }}</td>
                    <td>{{ player.playerDetails?.country || 'N/A' }}</td>
                    <td>{{ player.playerDetails?.rank || 'N/A' }}</td>
                    <td>{{ player.playerDetails?.elo || 'N/A' }}</td>
                    <td>
                        <button class="btn btn-primary btn-sm me-2" @click="viewPlayer(player.id)">View</button>
                        <button class="btn btn-danger btn-sm" @click="showDeleteModal(player.id)">Delete</button>
                    </td>
                </tr>
            </tbody>
        </table>

        <!-- Delete Confirmation Modal -->
        <div v-if="showModal" class="modal-backdrop">
            <div class="modal-content">
                <h5>Confirm Deletion</h5>
                <p>Are you sure you want to delete this player?</p>
                <div class="modal-actions">
                    <button class="btn btn-secondary" @click="cancelDelete">Cancel</button>
                    <button class="btn btn-danger" @click="deletePlayer(playerToDelete)">Delete</button>
                </div>
            </div>
        </div>

        <!-- Notification Message -->
        <div v-if="notificationMessage" class="notification">{{ notificationMessage }}</div>
    </div>
    </div>
</template>

<script>
import Navbar from '@/components/NavbarComponent.vue';
import axios from 'axios';

export default {
    components: {
        Navbar,
    },
    data() {
        return {
            players: [],
            sortColumn: 'name',
            showModal: false,
            playerToDelete: null,
            notificationMessage: '', // Holds the notification message
        };
    },
    computed: {
        sortedPlayers() {
            return [...this.players].sort((a, b) => {
                const colA = a[this.sortColumn]?.toString().toLowerCase() || '';
                const colB = b[this.sortColumn]?.toString().toLowerCase() || '';
                if (colA < colB) return -1;
                if (colA > colB) return 1;
                return 0;
            });
        },
    },
    created() {
        this.fetchPlayers();
    },
    methods: {
        async fetchPlayers() {
            try {
                const response = await axios.get('http://localhost:8080/users');
                this.players = response.data;
            } catch (error) {
                console.error('Error fetching players:', error);
            }
        },
        viewPlayer(id) {
            this.$router.push(`/player/${id}`);
        },
        showDeleteModal(id) {
            this.showModal = true;
            this.playerToDelete = id;
        },
        cancelDelete() {
            this.showModal = false;
            this.playerToDelete = null;
        },
        async deletePlayer(id) {
            try {
                await axios.delete(`http://localhost:8080/user/${id}`);
                this.displayNotification('Player deleted successfully');
                this.fetchPlayers();
            } catch (error) {
                console.error('Error deleting player:', error);
                this.displayNotification('Failed to delete player');
            } finally {
                this.showModal = false;
                this.playerToDelete = null;
            }
        },
        displayNotification(message) {
            this.notificationMessage = message;
            setTimeout(() => {
                this.notificationMessage = ''; // Clear notification after 3 seconds
            }, 3000);
        },
    },
};
</script>

<style scoped>
.all-players-container {
    background-color: #f3eeea;
}
.container {
    background-color: #f3f4f6;
    border-radius: 10px;
    padding: 20px;
    box-shadow: 0px 4px 8px rgba(0, 0, 0, 0.1);
    font-family: 'Arial', sans-serif;
}

h1 {
    color: #4b5563;
    font-size: 28px;
    margin-bottom: 20px;
}

.sort-container {
    display: flex;
    justify-content: flex-start;
    align-items: center;
    margin-bottom: 15px;
}

.sort-label {
    margin-right: 10px;
    font-weight: bold;
    color: #4b5563;
}

.form-select {
    width: 200px;
    border-radius: 5px;
    padding: 5px;
    font-size: 16px;
    color: #4b5563;
    border: 1px solid #d1d5db;
}

.table {
    border-collapse: separate;
    width: 100%;
    border-radius: 8px;
    overflow: hidden;
}

.table-header {
    background-color: #4b5563;
    color: white;
}

.table thead th {
    font-weight: bold;
    padding: 12px;
    text-align: left;
    color: #fff;
}

.table tbody tr {
    background-color: #ffffff;
    transition: background-color 0.2s;
}

.table tbody tr:hover {
    background-color: #f1f5f9;
}

.btn {
    border-radius: 5px;
    padding: 6px 12px;
    margin: 0px 10px;
}

.btn-primary {
    background-color: #b0a695;
    color: white;
    border: none;
}

.btn-primary:hover {
    background-color: #776b5d;
}


.btn-danger {
    background-color: #dd4f4f;
    border: none;
    color: white;
}

.btn-danger:hover {
    background-color: #b91c1c;
}

/* Modal styling */
.modal-backdrop {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-color: rgba(0, 0, 0, 0.5);
    display: flex;
    justify-content: center;
    align-items: center;
}

.modal-content {
    background-color: white;
    padding: 20px;
    width: 300px;
    border-radius: 8px;
    text-align: center;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}

.modal-actions {
    display: flex;
    justify-content: space-between;
    margin-top: 20px;
}

.modal-actions .btn {
    width: 48%;
}

/* Notification styling */
.notification {
    position: fixed;
    top: 20px;
    right: 20px;
    background-color: #776b5d;
    color: white;
    padding: 10px 20px;
    border-radius: 5px;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
    font-weight: bold;
    z-index: 1000;
}
</style>
