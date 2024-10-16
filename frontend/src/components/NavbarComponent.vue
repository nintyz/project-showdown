<template>
    <nav class="navbar navbar-expand-lg navbar-light bg-light">
        <div class="container-fluid">
            <router-link to="/" class="navbar-brand">
                <img src="@/assets/logo.png" alt="Logo" class="logo" />
            </router-link>

            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav"
                aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
            </button>

            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav ms-auto">
                    <!-- Links for All Pages -->
                    <li v-if="role === 'player' || role === 'admin'" class="nav-item">
                        <router-link :to="'/dashboard'" class="nav-link" :class="{ active: isActive('/dashboard') }">Dashboard</router-link>
                    </li>

                    <!-- Links for Player Pages -->
                    <li v-if="role === 'player'" class="nav-item">
                        <router-link :to="'/player-profile'" class="nav-link" :class="{ active: isActive('/player-profile') }">Player Profile</router-link>
                    </li>

                    <!-- Links for Admin Pages -->
                    <li v-if="role === 'admin'" class="nav-item">
                        <router-link :to="'/admin-dashboard'" class="nav-link" :class="{ active: isActive('/admin-dashboard') }">Admin Dashboard</router-link>
                    </li>
                    <li v-if="role === 'admin'" class="nav-item">
                        <router-link :to="'/new-tournament'" class="nav-link" :class="{ active: isActive('/new-tournament') }">Add Tournament</router-link>
                    </li>

                    <!-- Links for Auth Pages -->
                    <li v-if="route === 'LoginPage'" class="nav-item">
                        <router-link :to="'/signup'" class="nav-link" :class="{ active: isActive('/signup') }">Sign Up</router-link>
                    </li>
                    <li v-if="route === 'SignUp'" class="nav-item">
                        <router-link :to="'/login'" class="nav-link" :class="{ active: isActive('/login') }">Login</router-link>
                    </li>
                </ul>
            </div>
        </div>
    </nav>
</template>

<script>
export default {
    data() {
        return {
            role: "player", // Hardcode the role for now, could be "admin" or "player"
        }
    },
    computed: {
        route() {
            return this.$route.name; // Get the current route name
        }
    },
    methods: {
        isActive(path) {
            return this.$route.path === path;
        }
    }
};
</script>

<style scoped>
.logo {
    width: 150px;
}

.navbar-nav .nav-link {
    margin-right: 20px;
    color: #776b5d;
    transition: color 0.3s ease;
}

.navbar-nav .nav-link:hover {
    color: #b0a695;
}

.navbar-nav .nav-link.active {
    font-weight: bold;
    color: #b0a695; /* Custom color for the active page */
    border-bottom: 2px solid #b0a695; /* Add a border at the bottom to indicate active state */
}

.navbar {
    background-color: #f3eeea;
}
</style>
