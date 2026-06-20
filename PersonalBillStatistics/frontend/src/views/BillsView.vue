<template>
  <div class="bills-page">
    <div class="page-header">
      <div>
        <p class="page-kicker">Transactions</p>
        <h2>收支记录</h2>
      </div>
      <button class="btn-add" @click="openModal(null)">+ 添加记录</button>
    </div>

    <div class="summary-grid">
      <section class="summary-card">
        <span>本页收入</span>
        <strong class="text-income">+{{ incomeTotal.toFixed(2) }}</strong>
      </section>
      <section class="summary-card">
        <span>本页支出</span>
        <strong class="text-expense">-{{ expenseTotal.toFixed(2) }}</strong>
      </section>
      <section class="summary-card">
        <span>本页结余</span>
        <strong :class="balanceTotal >= 0 ? 'text-income' : 'text-expense'">
          {{ balanceTotal >= 0 ? '+' : '' }}{{ balanceTotal.toFixed(2) }}
        </strong>
      </section>
    </div>

    <div class="filter-bar">
      <input type="date" v-model="filters.start" class="filter-input" />
      <span>至</span>
      <input type="date" v-model="filters.end" class="filter-input" />
      <select v-model="filters.type" class="filter-select">
        <option value="">全部类型</option>
        <option value="income">收入</option>
        <option value="expense">支出</option>
      </select>
      <select v-model="filters.categoryId" class="filter-select">
        <option value="">全部分类</option>
        <option v-for="c in filteredCategories" :key="c.id" :value="c.id">{{ c.name }}</option>
      </select>
      <button class="btn-search" @click="search">查询</button>
    </div>

    <table class="bill-table" v-if="bills.length > 0">
      <thead>
        <tr>
          <th>日期</th>
          <th>类型</th>
          <th>分类</th>
          <th>金额</th>
          <th>备注</th>
          <th>操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="bill in bills" :key="bill.id">
          <td data-label="日期">{{ bill.date }}</td>
          <td data-label="类型">
            <span :class="bill.type === 'income' ? 'tag-income' : 'tag-expense'">
              {{ bill.type === 'income' ? '收入' : '支出' }}
            </span>
          </td>
          <td data-label="分类">{{ bill.categoryName }}</td>
          <td data-label="金额" :class="bill.type === 'income' ? 'text-income' : 'text-expense'">
            {{ bill.type === 'income' ? '+' : '-' }}{{ bill.amount }}
          </td>
          <td data-label="备注">{{ bill.remark || '-' }}</td>
          <td data-label="操作">
            <button class="btn-edit" @click="openModal(bill)">编辑</button>
            <button class="btn-delete" @click="handleDelete(bill.id)">删除</button>
          </td>
        </tr>
      </tbody>
    </table>
    <p class="empty-tip" v-else>暂无记录，点击"添加记录"开始记账吧</p>

    <div class="pagination" v-if="totalPages > 1">
      <button :disabled="page <= 1" @click="goPage(page - 1)">上一页</button>
      <span v-for="p in visiblePages" :key="p"
            :class="{ active: p === page }"
            @click="goPage(p)">{{ p }}</span>
      <button :disabled="page >= totalPages" @click="goPage(page + 1)">下一页</button>
      <span class="page-info">共 {{ total }} 条</span>
    </div>

    <!-- Add/Edit Modal -->
    <div class="modal-overlay" v-if="showModal" @click.self="showModal = false">
      <div class="modal-card">
        <h3>{{ editingBill ? '编辑记录' : '添加记录' }}</h3>
        <form @submit.prevent="handleSubmit">
          <div class="form-row">
            <label>类型</label>
            <select v-model="form.type" required>
              <option value="expense">支出</option>
              <option value="income">收入</option>
            </select>
          </div>
          <div class="form-row">
            <label>分类</label>
            <select v-model="form.categoryId" required>
              <option value="">请选择分类</option>
              <option v-for="c in filteredCategories" :key="c.id" :value="c.id">{{ c.name }}</option>
            </select>
          </div>
          <div class="form-row">
            <label>金额</label>
            <input v-model.number="form.amount" type="number" step="0.01" min="0.01" required placeholder="请输入金额" />
          </div>
          <div class="form-row">
            <label>日期</label>
            <input v-model="form.date" type="date" required />
          </div>
          <div class="form-row">
            <label>备注</label>
            <input v-model="form.remark" type="text" placeholder="备注（选填）" />
          </div>
          <div class="modal-actions">
            <button type="button" class="btn-cancel" @click="showModal = false">取消</button>
            <button type="submit" class="btn-primary">{{ editingBill ? '保存' : '添加' }}</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import { getBills, createBill, updateBill, deleteBill } from '../api/bill'
import { getCategories } from '../api/category'

export default {
  name: 'BillsView',
  data() {
    return {
      bills: [],
      categories: [],
      filters: { start: '', end: '', type: '', categoryId: '' },
      page: 1,
      pageSize: 10,
      total: 0,
      showModal: false,
      editingBill: null,
      form: { type: 'expense', categoryId: '', amount: null, date: '', remark: '' }
    }
  },
  computed: {
    filteredCategories() {
      if (!this.filters.type) return this.categories
      return this.categories.filter(c => c.type === this.filters.type)
    },
    totalPages() {
      return Math.max(1, Math.ceil(this.total / this.pageSize))
    },
    visiblePages() {
      const pages = []
      const total = this.totalPages
      let start = Math.max(1, this.page - 2)
      let end = Math.min(total, this.page + 2)
      if (end - start < 4) {
        if (start === 1) end = Math.min(total, start + 4)
        else start = Math.max(1, end - 4)
      }
      for (let i = start; i <= end; i++) pages.push(i)
      return pages
    },
    incomeTotal() {
      return this.bills
        .filter(bill => bill.type === 'income')
        .reduce((sum, bill) => sum + Number(bill.amount || 0), 0)
    },
    expenseTotal() {
      return this.bills
        .filter(bill => bill.type === 'expense')
        .reduce((sum, bill) => sum + Number(bill.amount || 0), 0)
    },
    balanceTotal() {
      return this.incomeTotal - this.expenseTotal
    }
  },
  async mounted() {
    await this.fetchCategories()
    this.fetchBills()
  },
  methods: {
    async fetchCategories() {
      const res = await getCategories()
      if (res.data.code === 200) this.categories = res.data.data
    },
    async fetchBills() {
      const params = { page: this.page, pageSize: this.pageSize }
      if (this.filters.start) params.start = this.filters.start
      if (this.filters.end) params.end = this.filters.end
      if (this.filters.type) params.type = this.filters.type
      if (this.filters.categoryId) params.categoryId = this.filters.categoryId
      const res = await getBills(params)
      if (res.data.code === 200) {
        this.bills = res.data.data.list
        this.total = res.data.data.total
        this.page = res.data.data.page
      }
    },
    search() {
      this.page = 1
      this.fetchBills()
    },
    goPage(p) {
      if (p < 1 || p > this.totalPages || p === this.page) return
      this.page = p
      this.fetchBills()
    },
    openModal(bill) {
      if (bill) {
        this.editingBill = bill
        this.form = {
          type: bill.type,
          categoryId: bill.categoryId,
          amount: bill.amount,
          date: bill.date,
          remark: bill.remark || ''
        }
      } else {
        this.editingBill = null
        this.form = { type: 'expense', categoryId: '', amount: null, date: new Date().toISOString().slice(0, 10), remark: '' }
      }
      this.showModal = true
    },
    async handleSubmit() {
      try {
        if (this.editingBill) {
          await updateBill(this.editingBill.id, this.form)
        } else {
          await createBill(this.form)
          this.page = 1
        }
        this.showModal = false
        this.fetchBills()
      } catch (e) {
        alert('操作失败')
      }
    },
    async handleDelete(id) {
      if (!confirm('确定要删除这条记录吗？')) return
      await deleteBill(id)
      if (this.bills.length === 1 && this.page > 1) {
        this.page--
      }
      this.fetchBills()
    }
  }
}
</script>

<style scoped>
.bills-page {
  display: grid;
  gap: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  gap: 16px;
}

.page-kicker {
  margin: 0 0 4px;
  color: var(--accent);
  font-size: 12px;
  font-weight: 800;
  letter-spacing: 0;
  text-transform: uppercase;
}

.page-header h2 {
  margin: 0;
  color: var(--ink);
  font-size: clamp(28px, 4vw, 42px);
  line-height: 1.1;
  letter-spacing: 0;
}

.btn-add,
.btn-search,
.btn-primary {
  border: 1px solid var(--accent-dark);
  border-radius: 14px;
  background: var(--accent);
  color: #fff;
  cursor: pointer;
  font-weight: 800;
}

.btn-add {
  padding: 0 18px;
  white-space: nowrap;
}

.btn-add:hover,
.btn-search:hover,
.btn-primary:hover {
  background: var(--accent-dark);
  transform: translateY(-1px);
}

.summary-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
}

.summary-card {
  min-height: 118px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: 20px;
  border: 1px solid var(--line);
  border-radius: 22px;
  background: var(--surface);
  box-shadow: var(--shadow);
}

.summary-card span {
  color: var(--muted);
  font-size: 13px;
  font-weight: 800;
}

.summary-card strong {
  font-size: clamp(24px, 3vw, 34px);
  line-height: 1;
  letter-spacing: 0;
}

.filter-bar {
  display: flex;
  gap: 10px;
  align-items: center;
  flex-wrap: wrap;
  padding: 16px;
  border: 1px solid var(--line);
  border-radius: 22px;
  background: var(--surface);
  box-shadow: var(--shadow);
}

.filter-bar span {
  color: var(--muted);
  font-size: 13px;
  font-weight: 800;
}

.filter-input,
.filter-select,
.form-row input,
.form-row select {
  min-height: 42px;
  border: 1px solid var(--line);
  border-radius: 14px;
  background: var(--surface-soft);
  color: var(--ink);
  outline: none;
}

.filter-input,
.filter-select {
  padding: 8px 10px;
  font-size: 14px;
}

.filter-input:focus,
.filter-select:focus,
.form-row input:focus,
.form-row select:focus {
  border-color: var(--accent);
  background: #fff;
  box-shadow: 0 0 0 4px rgba(36, 107, 254, 0.1);
}

.btn-search {
  padding: 0 18px;
}

.bill-table {
  width: 100%;
  border-collapse: separate;
  border-spacing: 0;
  overflow: hidden;
  border: 1px solid var(--line);
  border-radius: 22px;
  background: var(--surface);
  box-shadow: var(--shadow);
}

.bill-table th,
.bill-table td {
  padding: 15px 16px;
  border-bottom: 1px solid var(--line);
  font-size: 14px;
  text-align: left;
}

.bill-table th {
  background: var(--surface-soft);
  color: var(--ink);
  font-size: 12px;
  font-weight: 900;
  letter-spacing: 0;
  text-transform: uppercase;
}

.bill-table tbody tr:hover {
  background: #f8fbff;
}

.bill-table tbody tr:last-child td {
  border-bottom: none;
}

.tag-income,
.tag-expense {
  display: inline-flex;
  min-height: 26px;
  align-items: center;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 800;
  padding: 0 10px;
}

.tag-income {
  background: var(--success-soft);
  color: var(--success);
}

.tag-expense {
  background: var(--danger-soft);
  color: var(--danger);
}

.text-income {
  color: var(--success);
  font-weight: 900;
}

.text-expense {
  color: var(--danger);
  font-weight: 900;
}

.btn-edit,
.btn-delete,
.btn-cancel {
  min-height: 34px;
  border-radius: 12px;
  cursor: pointer;
  font-size: 12px;
  font-weight: 800;
  padding: 0 12px;
}

.btn-edit {
  margin-right: 6px;
  border: 1px solid var(--line);
  background: transparent;
  color: var(--accent);
}

.btn-edit:hover {
  background: var(--accent-soft);
  border-color: var(--accent);
}

.btn-delete {
  border: 1px solid var(--danger);
  background: transparent;
  color: var(--danger);
}

.btn-delete:hover {
  background: var(--danger-soft);
}

.empty-tip {
  margin: 0;
  border: 1px dashed var(--line-strong);
  border-radius: 22px;
  background: var(--surface);
  color: var(--muted);
  font-weight: 700;
  padding: 54px 20px;
  text-align: center;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 2px;
}

.pagination button,
.pagination span {
  min-height: 36px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border-radius: 12px;
  font-size: 13px;
  font-weight: 800;
  padding: 0 12px;
}

.pagination button {
  border: 1px solid var(--line);
  background: var(--surface);
  color: var(--ink);
  cursor: pointer;
}

.pagination button:disabled {
  color: #a7aeb8;
  cursor: not-allowed;
  opacity: 0.55;
}

.pagination button:hover:not(:disabled),
.pagination span:not(.active):not(.page-info):hover {
  border-color: var(--accent);
  background: var(--accent-soft);
  color: var(--accent-dark);
}

.pagination span {
  cursor: pointer;
}

.pagination span.active {
  background: var(--accent);
  color: #fff;
}

.pagination .page-info {
  color: var(--muted);
  cursor: default;
}

.modal-overlay {
  position: fixed;
  inset: 0;
  z-index: 100;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
  background: rgba(23, 32, 51, 0.54);
}

.modal-card {
  width: min(100%, 440px);
  max-height: calc(100dvh - 40px);
  overflow: auto;
  border: 1px solid var(--line);
  border-radius: 24px;
  background: var(--surface);
  box-shadow: 0 26px 80px rgba(0, 0, 0, 0.28);
  padding: 28px;
}

.modal-card h3 {
  margin: 0 0 20px;
  color: var(--ink);
  font-size: 22px;
}

.form-row {
  margin-bottom: 15px;
}

.form-row label {
  display: block;
  margin-bottom: 7px;
  color: var(--ink);
  font-size: 13px;
  font-weight: 800;
}

.form-row input,
.form-row select {
  width: 100%;
  padding: 9px 10px;
  font-size: 14px;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 22px;
}

.btn-cancel {
  border: 1px solid var(--line);
  background: transparent;
  color: var(--ink);
}

.btn-cancel:hover {
  background: var(--warning-soft);
}

.btn-primary {
  padding: 0 18px;
}

@media (max-width: 760px) {
  .page-header {
    align-items: stretch;
    flex-direction: column;
  }

  .btn-add,
  .btn-search {
    width: 100%;
  }

  .summary-grid {
    grid-template-columns: 1fr;
  }

  .filter-bar {
    display: grid;
    grid-template-columns: 1fr;
  }

  .filter-bar span {
    display: none;
  }

  .bill-table,
  .bill-table tbody,
  .bill-table tr,
  .bill-table td {
    display: block;
    width: 100%;
  }

  .bill-table {
    border: none;
    background: transparent;
    box-shadow: none;
  }

  .bill-table thead {
    display: none;
  }

  .bill-table tr {
    margin-bottom: 12px;
    border: 1px solid var(--line);
    border-radius: 18px;
    background: var(--surface);
    box-shadow: var(--shadow);
    padding: 10px 12px;
  }

  .bill-table td {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 16px;
    border-bottom: 1px solid var(--line);
    padding: 11px 0;
    text-align: right;
  }

  .bill-table td::before {
    content: attr(data-label);
    color: var(--muted);
    font-size: 12px;
    font-weight: 900;
    text-align: left;
  }

  .bill-table td:last-child {
    border-bottom: none;
  }
}
</style>
