<template>
  <div id="app">
    <router-view></router-view>
    <ChatbotComponent v-if="$route.path !== '/'" />
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
