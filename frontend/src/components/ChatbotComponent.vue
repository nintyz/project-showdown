<template>
  <div>
    <div v-if="isChatOpen" class="chatbot-window">
      <div class="chatbot-header">
        <span>ShowdownBot</span>
        <button class="close-btn" @click="toggleChat">X</button>
      </div>

      <div class="chatbot-body" ref="chatBody">
        <div v-for="message in messages" :key="message.id" class="message-container">
          <!-- User Messages -->
          <div v-if="message.sender === 'user'" class="d-flex flex-row-reverse">
            <div class="user-message">
              <p style="margin-bottom: 0;">{{ message.text }}</p>
            </div>
          </div>
          <!-- Bot Messages -->
          <div v-if="message.sender === 'bot'" class="d-flex">
            <div class="bot-message">
              <p style="margin-bottom: 0;">{{ message.text }}</p>
            </div>
          </div>
        </div>
      </div>

      <div class="chatbot-input">
        <textarea
          v-model="userInput"
          type="text"
          placeholder="Type a message..."
          @keyup.enter="sendMessage"
          rows="1"
          class="chatbot-textarea"
        ></textarea>
        <img 
          src="@/assets/send.png" 
          alt="Send" 
          class="send-btn" 
          @click="sendMessage"
        />
      </div>
    </div>

    <button v-if="!isChatOpen" @click="toggleChat" class="chatbot-toggle-btn">
      <img src="@/assets/chatbot.png" alt="Chatbot Icon" />
    </button>
  </div>
</template>

<script>
export default {
  data() {
    return {
      isChatOpen: false,
      userInput: '',
      messages: [],
    };
  },
  methods: {
    toggleChat() {
      this.isChatOpen = !this.isChatOpen;
    },
    sendMessage() {
      if (this.userInput.trim() !== '') {
        // Add user message to the messages array
        const userMessage = {
          id: Date.now(),
          text: this.userInput,
          sender: 'user',
        };
        this.messages.push(userMessage);
        
        this.userInput = '';

        setTimeout(() => {
          // Add bot message to the messages array
          const botMessage = {
            id: Date.now() + 1,
            text: "I'm just a bot, but I'm doing well!", // Example response
            sender: 'bot',
          };
          this.messages.push(botMessage);
      
          this.scrollToBottom();
        }, 1000);
        
        this.scrollToBottom();
      }
    },
    scrollToBottom() {
      this.$nextTick(() => {
        const chatBody = this.$refs.chatBody;
        chatBody.scrollTop = chatBody.scrollHeight;
      });
    },
  },
};
</script>

<style scoped>
.chatbot-window {
  position: fixed;
  bottom: 20px;
  right: 20px;
  width: 300px;
  height: 400px;
  background-color: white;
  border-radius: 10px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
  z-index: 1000;
  display: flex;
  flex-direction: column;
}

.chatbot-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #007bff;
  color: white;
  padding: 10px;
  border-top-left-radius: 10px;
  border-top-right-radius: 10px;
}

.chatbot-body {
  padding: 20px;
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow-y: auto;
  max-height: 300px;
}

.user-message,
.bot-message {
  margin: 5px 0;
  padding: 10px;
  border-radius: 10px;
  max-width: 70%;
  word-wrap: break-word;
}

.user-message {
  background-color: #007bff;
  color: white;
  align-self: flex-end;
}

.bot-message {
  background-color: #f1f1f1;
  align-self: flex-start;
}

.chatbot-input {
  display: flex;
  padding: 10px;
  border-top: 1px solid #ccc;
}

.chatbot-textarea {
  flex: 1;
  margin-right: 8px;
  border-radius: 4px;
  border: none;
  resize: none;
}

.chatbot-textarea:focus {
  outline: none;
}

.send-btn {
  width: 30px;
  height: 30px;
  margin: auto;
  cursor: pointer;
}

.close-btn {
  background: none;
  border: none;
  color: white;
  cursor: pointer;
}

.chatbot-toggle-btn {
  position: fixed;
  bottom: 20px;
  right: 20px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 50%;
  padding: 15px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
  cursor: pointer;
}

.chatbot-toggle-btn img {
  width: 30px;
  height: 30px;
}
</style>
