<template>
  <div id="app">
    <router-view></router-view>
    <ChatbotComponent v-if="$route.path !== '/' && $route.path !== '/login' && $route.path !== '/signup' && $route.path !== '/verify' && $route.path !== '/verify-2fa'"/>
  </div>
</template>

<script>
import ChatbotComponent from './components/ChatbotComponent.vue'; 

export default {
  name: 'App',
  components: {
    ChatbotComponent,
  },
}
</script>
