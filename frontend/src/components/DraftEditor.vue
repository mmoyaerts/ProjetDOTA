<template>
  <div style="margin-top: 1rem;">
    <h3>Draft Editor</h3>
    <div style="display: flex; gap: 1rem; margin-top: 0.5rem;">
      <TeamDraft team="Radiant" :bans="bansRadiant" :picks="picksRadiant" :heroes="heroes" @select="onSelect" />
      <TeamDraft team="Dire"    :bans="bansDire"    :picks="picksDire"    :heroes="heroes" @select="onSelect" />
    </div>
    <button @click="sendPredict" style="margin-top: 1rem; padding: 0.5rem 1rem; background: #3b82f6; color: white; border: none; border-radius: 4px; cursor: pointer;">Envoyer vers IA</button>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import TeamDraft from './TeamDraft.vue';
const props = defineProps({ heroes: Array });
const emits = defineEmits(['predict']);

const bansRadiant = ref([]);
const picksRadiant = ref([]);
const bansDire = ref([]);
const picksDire = ref([]);

function onSelect({ team, phase, hero }) {
  const list = phase === 'ban' ? (team === 'Radiant' ? bansRadiant : bansDire) : (team === 'Radiant' ? picksRadiant : picksDire);
  if (!list.value.includes(hero.id)) list.value.push(hero.id);
}

function sendPredict() {
  emits('predict', { radiant: Math.random(), dire: Math.random() });
}
</script>