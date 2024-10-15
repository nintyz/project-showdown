import { createRouter, createWebHistory } from 'vue-router'

import LoginPage from '@/views/LoginPage.vue'
import DashboardPage from '@/views/DashboardPage.vue'
import PlayerProfile from '@/views/PlayerProfile.vue'
import AdminDashboard from '@/views/AdminDashboard.vue'
import AddTournament from '@/views/AddTournament.vue'
import SignUp from '@/views/SignUp.vue'

const routes = [
    {
        path: '/login',
        name: 'LoginPage',
        component: LoginPage,
    },
    {
        path: '/signup',
        name: 'SignUp',
        component: SignUp,
    },
    {
        path: '/dashboard',
        name: 'DashboardPage',
        component: DashboardPage,
    },
    {
        path: '/player-profile',
        name: 'PlayerProfile',
        component: PlayerProfile,
    },
    {
        path: '/admin-dashboard',
        name: 'AdminDashboard',
        component: AdminDashboard,
    },
    {
        path: '/new-tournament',
        name: 'AddTournament',
        component: AddTournament,
    },
]

const router = createRouter({
    history: createWebHistory(process.env.BASE_URL),
    routes
})

export default router
