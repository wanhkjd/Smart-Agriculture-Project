<template>
  <div class="auth-container">
    <section class="auth-intro">
      <p class="eyebrow">Create Account</p>
      <h1>开启一套新的财务工作台。</h1>
      <p>注册后保存账单、管理分类，并用趋势图快速查看每个月的资金变化。</p>
      <div class="auth-preview">
        <span></span>
        <strong>预算从第一笔开始</strong>
        <small>注册后创建你的个人账本空间</small>
      </div>
    </section>
    <div class="auth-card">
      <h2>注册</h2>
      <form @submit.prevent="handleRegister">
        <div class="form-group">
          <label>用户名</label>
          <input v-model="form.username" type="text" required placeholder="请输入用户名" />
        </div>
        <div class="form-group">
          <label>密码</label>
          <input v-model="form.password" type="password" required placeholder="请输入密码" />
        </div>
        <div class="form-group">
          <label>邮箱</label>
          <input v-model="form.email" type="email" placeholder="请输入邮箱（选填）" />
        </div>
        <p class="error-msg" v-if="error">{{ error }}</p>
        <p class="success-msg" v-if="success">{{ success }}</p>
        <button type="submit" class="btn-primary">注册</button>
      </form>
      <p class="switch-link">
        已有账号？<router-link to="/login">立即登录</router-link>
      </p>
    </div>
  </div>
</template>

<script>
import { register } from '../api/user'

export default {
  name: 'RegisterView',
  data() {
    return {
      form: { username: '', password: '', email: '' },
      error: '',
      success: ''
    }
  },
  methods: {
    async handleRegister() {
      this.error = ''
      this.success = ''
      try {
        const res = await register(this.form)
        if (res.data.code === 200) {
          this.success = '注册成功，即将跳转到登录页...'
          setTimeout(() => this.$router.push('/login'), 1500)
        } else {
          this.error = res.data.message
        }
      } catch (e) {
        if (e.response) {
          this.error = e.response.data?.message || '服务器错误(' + e.response.status + ')'
        } else if (e.request) {
          this.error = '无法连接到服务器，请确认后端已启动(端口8080)'
        } else {
          this.error = e.message
        }
      }
    }
  }
}
</script>

<style scoped>
.auth-container {
  min-height: 100dvh;
  display: grid;
  grid-template-columns: minmax(0, 1.08fr) minmax(320px, 420px);
  align-items: center;
  gap: clamp(28px, 6vw, 72px);
  padding: clamp(28px, 7vw, 84px) 0;
}

.auth-intro {
  max-width: 610px;
}

.eyebrow {
  margin: 0 0 14px;
  color: var(--accent);
  font-size: 13px;
  font-weight: 800;
  letter-spacing: 0;
  text-transform: uppercase;
}

.auth-intro h1 {
  margin: 0;
  color: var(--ink);
  font-size: clamp(38px, 6vw, 68px);
  line-height: 1.02;
  letter-spacing: 0;
}

.auth-intro p:not(.eyebrow) {
  max-width: 520px;
  margin: 22px 0 0;
  color: var(--muted);
  font-size: 17px;
}

.auth-preview {
  width: min(100%, 380px);
  margin-top: 34px;
  padding: 22px;
  border: 1px solid var(--line);
  border-radius: 24px;
  background: linear-gradient(135deg, #ffffff 0%, #f8fbff 100%);
  box-shadow: var(--shadow);
}

.auth-preview span {
  display: block;
  width: 58px;
  height: 8px;
  border-radius: 99px;
  background: var(--cyan);
}

.auth-preview strong {
  display: block;
  margin-top: 28px;
  color: var(--ink);
  font-size: 40px;
  line-height: 1;
}

.auth-preview small {
  display: block;
  margin-top: 8px;
  color: var(--muted);
  font-weight: 700;
}

.auth-card {
  width: 100%;
  padding: 34px;
  border: 1px solid var(--line);
  border-radius: 24px;
  background: rgba(255, 255, 255, 0.94);
  box-shadow: var(--shadow);
}

.auth-card h2 {
  margin: 0 0 26px;
  color: var(--ink);
  font-size: 24px;
  letter-spacing: 0;
}

.form-group {
  margin-bottom: 18px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  color: var(--ink);
  font-size: 14px;
  font-weight: 700;
}

.form-group input {
  width: 100%;
  min-height: 46px;
  padding: 10px 14px;
  border: 1px solid var(--line);
  border-radius: 14px;
  background: var(--surface-soft);
  color: var(--ink);
  font-size: 15px;
  outline: none;
}

.form-group input:focus {
  border-color: var(--accent);
  background: #fff;
  box-shadow: 0 0 0 4px rgba(36, 107, 254, 0.1);
}

.error-msg,
.success-msg {
  margin: 0 0 14px;
  font-size: 13px;
  font-weight: 700;
  padding: 9px 10px;
}

.error-msg {
  border-left: 3px solid var(--danger);
  background: var(--danger-soft);
  color: var(--danger);
}

.success-msg {
  border-left: 3px solid var(--success);
  background: var(--success-soft);
  color: var(--success);
}

.btn-primary {
  width: 100%;
  min-height: 46px;
  border: 1px solid var(--accent-dark);
  border-radius: 14px;
  background: var(--accent);
  color: #fff;
  cursor: pointer;
  font-size: 15px;
  font-weight: 800;
}

.btn-primary:hover {
  background: var(--accent-dark);
  transform: translateY(-1px);
}

.switch-link {
  margin: 20px 0 0;
  color: var(--muted);
  font-size: 14px;
  text-align: center;
}

.switch-link a {
  color: var(--accent);
  font-weight: 800;
}

@media (max-width: 820px) {
  .auth-container {
    grid-template-columns: 1fr;
    padding-top: 32px;
  }

  .auth-intro h1 {
    font-size: 38px;
  }
}
</style>
