<template>
    <!-- Navbar -->
    <Navbar />
    <div class="admin-dashboard container mt-3">
        <!-- Header Section -->
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h2>All tournaments</h2>
            <button class="btn btn-outline-dark" @click="addNewTournament">
                <i class="bi bi-plus"></i> Add New
            </button>
        </div>

        <!-- Tournament List -->
        <div v-for="(tournament, index) in tournaments" :key="index" class="tournament-card row mb-3 p-3">
            <div class="col-md-2 d-flex align-items-center">
                <img :src="tournament.logo" alt="Tournament Logo" class="img-fluid tournament-logo" />
            </div>
            <div class="col-md-6 d-flex flex-column justify-content-center">
                <h4>{{ tournament.name }}</h4>
                <p>{{ tournament.location }} | {{ tournament.date }}</p>
            </div>
            <div class="col-md-2 d-flex align-items-center">
                <span :class="getStatusClass(tournament.status)">{{ tournament.status }}</span>
            </div>
            <div class="col-md-2 d-flex justify-content-end align-items-center">
                <button class="btn btn-outline-primary me-2">view</button>
                <button class="btn btn-outline-secondary me-2">edit</button>
                <button class="btn btn-icon">
                    <img src="@/assets/remove.png" alt="Icon" />
                </button>
            </div>
        </div>
    </div>
</template>

<script>
import Navbar from '@/components/NavbarComponent.vue';
import '@/assets/main.css'; 
export default {
    components: {

        Navbar,
    },
    data() {
        return {
            tournaments: [
                {
                    name: "Wimbledon",
                    location: "London, UK",
                    date: "1 - 14 July 2024",
                    status: "Upcoming",
                    logo: "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSg7iZb7c9r9kR4Quz_H0M9bgyeYyaPvJxJ7_hANon_A2V0EqjG1PmkuXowyF_ums6icjk&usqp=CAU",
                },
                {
                    name: "US Open",
                    location: "New York, USA",
                    date: "26 August - 8 September 2024",
                    status: "Upcoming",
                    logo: "https://upload.wikimedia.org/wikipedia/commons/thumb/c/cd/Usopen-horizontal-logo.svg/1200px-Usopen-horizontal-logo.svg.png",
                },
                {
                    name: "ATP Finals",
                    location: "Turin, Italy",
                    date: "10 - 17 November 2023",
                    status: "Ongoing",
                    logo: "https://cdn.cookielaw.org/logos/d650bf03-392a-4f58-9e0f-30e4e5889bc1/4166a46d-b503-4394-9a8d-cfc6856bb183/d11831c3-5497-41f0-bcc8-a7d664ee017f/nitto.png",
                },
                {
                    name: "French Open",
                    location: "Paris, France",
                    date: "22 May - 5 June 2023",
                    status: "Ended",
                    logo: "https://w7.pngwing.com/pngs/236/449/png-transparent-stade-roland-garros-2018-french-open-2007-french-open-2017-french-open-tennis-grand-finale-text-trademark-sport.png",
                },
            ],
        };
    },
    methods: {
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
        addNewTournament() {
            this.$router.push("/new-tournament");
        },
    },
};
</script>

<style scoped>
.admin-dashboard {
    background-color: #f3eeea;
    padding: 30px;
}

h2 {
    font-family: 'Arial', sans-serif;
    color: #776b5d;
}

.tournament-card {
    background-color: #fff;
    border-radius: 8px;
    box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

.tournament-logo {
    width: 80px;
    height: auto;
}

.tournament-card h4 {
    font-size: 18px;
    color: #776b5d;
}

.tournament-card p {
    margin: 0;
    font-size: 14px;
    color: #776b5d;
}

.btn-outline-primary {
    background-color: #b0a695;
    color: white;
    border: none;
}

.btn-outline-primary:hover {
    background-color: #776b5d;
}

.btn-outline-secondary {
    border-color: #776b5d;
    color: #776b5d;
}

.btn-outline-secondary:hover {
    background-color: #776b5d;
    color: white;
}

.btn-danger {
    color: white;
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

.btn-icon img {
    width: 24px;
    /* Adjust the size of the image */
    height: 24px;
    /* Adjust the size of the image */
}
</style>