<template>
  <div class="container">
    <div class="card">
      <img src="../assets/logo.svg" alt="Logo" class="logo" />
      <h1>Créer un compte</h1>
      <form @submit.prevent="onSubmit">
        <label>Nom d'utilisateur</label>
        <input v-model="username" type="text" required />
        <label>Email</label>
        <input v-model="email" type="email" required />
        <label>Mot de passe</label>
        <input v-model="password" type="password" required />
        <button type="submit">S'inscrire</button>
      </form>
      <p v-if="error" style="color: #f87171; text-align: center;">{{ error }}</p>
      <div class="link-group">
        <router-link to="/">Retour à la connexion</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const username = ref('')
const email    = ref('')
const password = ref('')
const error    = ref('')
const router   = useRouter()

async function onSubmit() {
  error.value = ''
  if (!username.value || !email.value || !password.value) {
    error.value = 'Tous les champs sont obligatoires'
    return
  }
  try {
     console.log("je suis là");
    // Appel à /api/auth/register
    await axios.post('http://localhost:8080/users', {
      username: username.value,
      email:    email.value,
      passwordHash: password.value
    })
    router.push({ name: 'Login' })
  } catch (err) {
    error.value = err.response?.data || 'Erreur lors de la création du compte'
  }
}
</script>

