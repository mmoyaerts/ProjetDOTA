<template>
  <div style="background: #374151; padding: 1rem; border-radius: 8px;">
    <h4>{{ team }}</h4>
    <div style="margin-top: 0.5rem;
               display:flex; gap: 0.5rem;">
      <div>
        <strong>Bans</strong>
        <div style="display:flex; gap:0.5rem; margin-top:0.25rem;">
          <img v-for="id in bans" :key="id" :src="`/assets/heroes/${id}.png`" style="width:24px; height:24px; border-radius:4px" />
        </div>
      </div>
      <div>
        <strong>Picks</strong>
        <div style="display:flex; gap:0.5rem; margin-top:0.25rem;">
          <img v-for="id in picks" :key="id" :src="`/assets/heroes/${id}.png`" style="width:32px; height:32px;" />
        </div>
      </div>
    </div>
    <div style="display:grid; grid-template-columns: repeat(5, 1fr); gap:0.5rem; margin-top:1rem;">
      <button v-for="hero in heroes" :key="hero.id" @click="$emit('select', { team, phase, hero })"
              :style="{
                border: '2px solid',
                borderColor: props.bans.includes(hero.id) ? '#f87171' : props.picks.includes(hero.id) ? '#34d399' : 'transparent',
                borderRadius: '4px',
                padding:0,
                cursor:'pointer'
              }">
        <img :src="`/assets/heroes/${hero.id}.png`" style="width:100%; height:auto; display:block;" />
      </button>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue';
const props = defineProps({ team: String, bans: Array, picks: Array, heroes: Array });
const phase = computed(() => (props.bans.length < 5 ? 'ban' : 'pick'));
</script>
