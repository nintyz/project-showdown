<template>
  <div id="app">
    <router-view></router-view>
    <ChatbotComponent />
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
