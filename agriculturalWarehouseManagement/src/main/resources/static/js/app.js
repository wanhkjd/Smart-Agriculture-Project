/**
 * 农产品电商仓配管理系统 - Vue 3 前端应用
 * 使用 CDN 加载 Vue 3 和 Element Plus
 */
const { createApp, ref, reactive, computed, onMounted, watch, nextTick } = Vue;

const API = '/api';
const $ = (sel) => document.querySelector(sel);

// Token 管理
function getToken() { return localStorage.getItem('token') || ''; }
function setToken(t) { localStorage.setItem('token', t); }
function clearToken() { localStorage.removeItem('token'); }

// 通用请求（自动携带 Authorization header）
function authHeaders() {
    const headers = { 'Content-Type': 'application/json' };
    const token = getToken();
    if (token) headers['Authorization'] = 'Bearer ' + token;
    return headers;
}

async function get(url) {
    const r = await fetch(API + url, { headers: authHeaders() });
    if (r.status === 401) { clearToken(); showLoginForce(); return { code: 401, message: '未登录' }; }
    if (r.status === 403) { const d = await r.json(); showWarning(d.message || '无权限操作，请联系管理员'); return d; }
    return r.json();
}
async function post(url, data) {
    const r = await fetch(API + url, { method: 'POST', headers: authHeaders(), body: JSON.stringify(data) });
    if (r.status === 401) { clearToken(); showLoginForce(); return { code: 401, message: '未登录' }; }
    if (r.status === 403) { const d = await r.json(); showWarning(d.message || '无权限操作，请联系管理员'); return d; }
    return r.json();
}
async function put(url, data) {
    const options = { method: 'PUT', headers: {} };
    // 始终携带 Authorization，仅在有 body 时设置 Content-Type
    const token = getToken();
    if (token) options.headers['Authorization'] = 'Bearer ' + token;
    if (data !== undefined) {
        options.headers['Content-Type'] = 'application/json';
        options.body = data instanceof FormData ? data : JSON.stringify(data);
    }
    const r = await fetch(API + url, options);
    if (r.status === 401) { clearToken(); showLoginForce(); return { code: 401, message: '未登录' }; }
    if (r.status === 403) { const d = await r.json(); showWarning(d.message || '无权限操作，请联系管理员'); return d; }
    return r.json();
}
async function del(url) {
    const r = await fetch(API + url, { method: 'DELETE', headers: authHeaders() });
    if (r.status === 401) { clearToken(); showLoginForce(); return { code: 401, message: '未登录' }; }
    if (r.status === 403) { const d = await r.json(); showWarning(d.message || '无权限操作，请联系管理员'); return d; }
    return r.json();
}

// 全局 showLogin 引用
let globalShowLogin = null;
function showLoginForce() {
    if (globalShowLogin) globalShowLogin.value = true;
}

// 全局权限不足提示（使用 Element Plus 消息）
function showWarning(msg) {
    if (typeof ElMessage !== 'undefined') {
        try { ElMessage.warning(msg || '无权限操作，请联系管理员'); } catch(e) {}
    }
    // 降级显示
    var fallback = document.getElementById('toast-fallback');
    if (!fallback) {
        fallback = document.createElement('div');
        fallback.id = 'toast-fallback';
        fallback.style.cssText = 'position:fixed;top:16px;right:16px;background:#e6a23c;color:#fff;padding:10px 20px;border-radius:6px;z-index:9999;display:none;';
        document.body.appendChild(fallback);
    }
    fallback.textContent = msg || '无权限操作，请联系管理员';
    fallback.style.display = 'block';
    setTimeout(function(){ fallback.style.display = 'none'; }, 3000);
}

const app = createApp({
    setup() {
        // ========== 状态 ==========
        const activeTab = ref('dashboard');
        const toastMsg = ref('');
        const toastType = ref('success');
        const currentUser = reactive({ id: null, username: '', realName: '', role: '' });

        const tabs = [
            { key: 'dashboard', label: '仪表盘' },
            { key: 'products', label: '商品管理' },
            { key: 'inventory', label: '库存管理' },
            { key: 'inbound', label: '入库管理' },
            { key: 'outbound', label: '出库拣货' },
            { key: 'orders', label: '订单管理' },
            { key: 'replenish', label: '补货管理' },
            { key: 'reports', label: '报表分析' },
            { key: 'users', label: '用户管理' },
            { key: 'system', label: '系统管理' },
        ];

        // 非管理员隐藏 用户管理 和 系统管理 标签
        const visibleTabs = computed(() => {
            if (currentUser.role === '管理员') return tabs;
            return tabs.filter(t => t.key !== 'users' && t.key !== 'system');
        });

        // 角色检查辅助（仅供显示角色标签，不做 UI 隐藏）
        const isAdmin = computed(() => currentUser.role === '管理员');
        const hasRole = (...roles) => roles.includes(currentUser.role);

        // 数据列表
        const dashboard = ref({});
        const products = ref([]);
        const inventories = ref([]);
        const inboundOrders = ref([]);
        const outboundOrders = ref([]);
        const customerOrders = ref([]);
        const pickingTasks = ref([]);
        const replenishTasks = ref([]);
        const operationLogs = ref([]);
        const reports = ref([]);
        const systemLogs = ref([]);
        const configs = ref({});
        const pickingStrategy = ref('FIFO');
        const availableStrategies = ref(['FIFO', 'FEFO', 'CATEGORY']);
        const users = ref([]);
        const deviceStates = ref([]);
        const expiringItems = ref([]);
        const lowStockItems = ref([]);
        const permissionOps = ref(['入库','出库','报损','盘点','补货','质检','拣货','用户管理','查看报表','配置管理']);
        const availableLocations = ref([]);
        const availablePickers = ref([]);
        const pickerSelectTaskId = ref(null);
        const selectedPickerName = ref('');
        const confirmDelete = ref({ visible: false, message: '', callback: null });

        // 库存子视图
        const inventoryView = ref('all'); // all | expiring | lowstock

        // 弹窗控制
        const showModal = ref(false);
        const modalTitle = ref('');
        const modalType = ref('');

        // 表单数据
        const productForm = reactive({ id: null, name: '', category: '', shelfLifeDays: 30, storageCondition: '常温', unit: 'kg', quantity: 1, description: '', status: 0, image: '' });
        const inboundForm = reactive({ productId: null, batchNo: '', quantity: 1, operator: '', remark: '', locationId: null, productionDate: '', supplier: '' });
        const outboundForm = reactive({ productId: null, quantity: 1, operator: '', pickingStrategy: 'FIFO', customerOrderId: null, matchedBatch: '' });
        const orderForm = reactive({ productId: null, quantity: 1, customerName: '', customerPhone: '', deliveryAddress: '' });
        const replenishForm = reactive({ productId: null, replenishQuantity: 1, reason: '低库存', creator: '' });
        const userForm = reactive({ id: null, username: '', password: '', realName: '', role: '仓管员', phone: '', status: 1 });

        // 搜索
        const productSearch = reactive({ category: '', keyword: '' });

        // ========== Toast ==========
        const showToast = (msg, type = 'success') => {
            toastMsg.value = msg; toastType.value = type;
            setTimeout(() => { toastMsg.value = ''; }, 2500);
        };

        // ========== 数据加载 ==========
        const loadDashboard = async () => {
            const r = await get('/reports/dashboard');
            if (r.code === 200) dashboard.value = r.data;
        };
        const chartData = ref(null);
        const barRef = ref(null);
        const pieRef = ref(null);
        let barInstance = null;
        let pieInstance = null;

        const loadChartData = async () => {
            if (activeTab.value !== 'dashboard') return;
            const r = await get('/reports/chart-data');
            if (r.code === 200) {
                chartData.value = r.data;
                setTimeout(drawCharts, 200);
            }
        };

        watch(activeTab, (tab) => {
            if (tab === 'dashboard') loadChartData();
        });

        const drawCharts = () => {
            if (typeof echarts === 'undefined') { console.warn('ECharts 未加载'); return; }
            const barEl = document.getElementById('barChart') || barRef.value;
            const pieEl = document.getElementById('pieChart') || pieRef.value;
            if (!barEl || !pieEl) { console.warn('图表容器未就绪'); return; }
            if (!chartData.value) { console.warn('图表数据为空'); return; }
            const d = chartData.value;

            if (barInstance) barInstance.dispose();
            barInstance = echarts.init(barEl);
            barInstance.setOption({
                tooltip: { trigger: 'axis' },
                xAxis: { type: 'category', data: d.productNames, axisLabel: { rotate: 30 } },
                yAxis: { type: 'value', name: '数量' },
                series: [{ type: 'bar', data: d.productQuantities, itemStyle: { color: '#1890ff' } }],
                grid: { left: '3%', right: '4%', bottom: '15%', containLabel: true }
            });

            if (pieInstance) pieInstance.dispose();
            pieInstance = echarts.init(pieEl);
            pieInstance.setOption({
                tooltip: { trigger: 'item', formatter: '{b}: {c} 种 ({d}%)' },
                legend: { bottom: 0 },
                series: [{
                    type: 'pie',
                    radius: ['40%', '70%'],
                    label: { formatter: '{b}\n{d}%' },
                    data: d.categoryPie
                }]
            });

            const onResize = () => { if (barInstance) barInstance.resize(); if (pieInstance) pieInstance.resize(); };
            window.removeEventListener('resize', onResize);
            window.addEventListener('resize', onResize);
        };
        const loadProducts = async () => {
            const r = await get('/products');
            if (r.code === 200) products.value = r.data;
        };
        const loadInventories = async () => {
            const r = await get('/inventory');
            if (r.code === 200) inventories.value = r.data;
        };
        const loadInboundOrders = async () => {
            const r = await get('/inbound');
            if (r.code === 200) inboundOrders.value = r.data;
        };
        const loadOutboundOrders = async () => {
            const r = await get('/outbound?page=' + outboundPage.page + '&pageSize=' + outboundPage.pageSize);
            if (r.code === 200) {
                if (r.data.records) { outboundOrders.value = r.data.records; outboundPage.total = r.data.total; outboundPage.totalPages = r.data.totalPages; }
                else outboundOrders.value = r.data;
            }
        };
        const loadCustomerOrders = async () => {
            const r = await get('/orders');
            if (r.code === 200) customerOrders.value = r.data;
        };
        const loadReplenishTasks = async () => {
            const r = await get('/replenish');
            if (r.code === 200) replenishTasks.value = r.data;
        };
        const reportPage = reactive({ page: 1, pageSize: 5, total: 0, totalPages: 0 });
        const logPage = reactive({ page: 1, pageSize: 10, total: 0, totalPages: 0 });
        const outboundPage = reactive({ page: 1, pageSize: 10, total: 0, totalPages: 0 });
        const pickingPage = reactive({ page: 1, pageSize: 10, total: 0, totalPages: 0 });

        const loadOperationLogs = async () => {
            const r = await get('/reports/operation-logs?page=' + logPage.page + '&pageSize=' + logPage.pageSize);
            if (r.code === 200) {
                if (r.data.records) { operationLogs.value = r.data.records; logPage.total = r.data.total; logPage.totalPages = r.data.totalPages; }
                else operationLogs.value = r.data;
            }
        };
        const loadReports = async () => {
            const r = await get('/reports/inventory-turnover?page=' + reportPage.page + '&pageSize=' + reportPage.pageSize);
            if (r.code === 200) {
                if (r.data.records) { reports.value = r.data.records; reportPage.total = r.data.total; reportPage.totalPages = r.data.totalPages; }
                else reports.value = r.data;
            }
        };
        const prevReportPage = () => { if (reportPage.page > 1) { reportPage.page--; loadReports(); } };
        const nextReportPage = () => { if (reportPage.page < reportPage.totalPages) { reportPage.page++; loadReports(); } };
        const prevLogPage = () => { if (logPage.page > 1) { logPage.page--; loadOperationLogs(); } };
        const nextLogPage = () => { if (logPage.page < logPage.totalPages) { logPage.page++; loadOperationLogs(); } };
        const prevOutboundPage = () => { if (outboundPage.page > 1) { outboundPage.page--; loadOutboundOrders(); } };
        const nextOutboundPage = () => { if (outboundPage.page < outboundPage.totalPages) { outboundPage.page++; loadOutboundOrders(); } };
        const prevPickingPage = () => { if (pickingPage.page > 1) { pickingPage.page--; loadPickingTasks(); } };
        const nextPickingPage = () => { if (pickingPage.page < pickingPage.totalPages) { pickingPage.page++; loadPickingTasks(); } };
        const loadSystemLogs = async () => {
            const r = await get('/system/logs');
            if (r.code === 200) systemLogs.value = r.data;
        };
        const loadConfigs = async () => {
            const r = await get('/system/config');
            if (r.code === 200) configs.value = r.data;
        };
        const loadUsers = async () => {
            const r = await get('/users');
            if (r.code === 200) users.value = r.data;
        };
        const loadPickingStrategy = async () => {
            const r = await get('/inventory/strategy');
            if (r.code === 200) {
                pickingStrategy.value = r.data.current;
                availableStrategies.value = r.data.available;
            }
        };
        const loadPickingTasks = async () => {
            const r = await get('/picking?page=' + pickingPage.page + '&pageSize=' + pickingPage.pageSize);
            if (r.code === 200) {
                if (r.data.records) { pickingTasks.value = r.data.records; pickingPage.total = r.data.total; pickingPage.totalPages = r.data.totalPages; }
                else pickingTasks.value = r.data;
            }
        };
        const loadDevices = async () => {
            const r = await get('/picking/devices');
            if (r.code === 200) deviceStates.value = r.data;
        };
        const loadExpiring = async () => {
            const r = await get('/inventory/expiring');
            if (r.code === 200) expiringItems.value = r.data;
        };
        const loadLowStock = async () => {
            const r = await get('/inventory/low-stock');
            if (r.code === 200) lowStockItems.value = r.data;
        };
        const refreshAll = () => {
            const m = {
                dashboard: () => { loadDashboard(); loadChartData(); },
                products: loadProducts,
                inventory: () => { loadInventories(); loadExpiring(); loadLowStock(); },
                inbound: loadInboundOrders,
                outbound: () => { loadOutboundOrders(); loadPickingTasks(); loadDevices(); },
                orders: loadCustomerOrders,
                replenish: loadReplenishTasks,
                reports: () => { loadReports(); loadOperationLogs(); },
                users: loadUsers,
                system: () => { loadSystemLogs(); loadConfigs(); },
            };
            if (m[activeTab.value]) m[activeTab.value]();
            loadDashboard();
        };

        onMounted(() => {
            loadDashboard();
            loadPickingStrategy();
            loadProducts();
            if (activeTab.value === 'dashboard') loadChartData();
        });

        // ========== Tab切换 ==========
        const switchTab = (tab) => {
            activeTab.value = tab;
            setTimeout(refreshAll, 50);
        };

        // ========== 商品操作 ==========
        const openProductForm = (p = null) => {
            if (p) {
                Object.assign(productForm, { id: p.id, name: p.name, category: p.category, shelfLifeDays: p.shelfLifeDays, storageCondition: p.storageCondition, unit: p.unit, quantity: p.quantity, description: p.description, status: p.status, image: p.image || '' });
                modalTitle.value = '编辑商品';
            } else {
                Object.assign(productForm, { id: null, name: '', category: '', shelfLifeDays: 30, storageCondition: '常温', unit: 'kg', quantity: 1, description: '', status: 0, image: '' });
                modalTitle.value = '新增商品';
            }
            modalType.value = 'product';
            showModal.value = true;
        };
        const saveProduct = async () => {
            // 如果选择了新图片，先上传
            if (productImageFile.value) {
                const fd = new FormData();
                fd.append('file', productImageFile.value);
                const uploadRes = await fetch(API + '/common/upload', {
                    method: 'POST',
                    headers: { 'Authorization': 'Bearer ' + getToken() },
                    body: fd
                });
                const uploadData = await uploadRes.json();
                if (uploadData.code === 200) {
                    productForm.image = uploadData.data;
                } else {
                    showToast(uploadData.message || '图片上传失败', 'error');
                    return;
                }
            }
            const r = productForm.id
                ? await put('/products/' + productForm.id, productForm)
                : await post('/products', productForm);
            if (r.code === 200) { showToast('保存成功'); showModal.value = false; productImageFile.value = null; loadProducts(); loadDashboard(); }
            else showToast(r.message, 'error');
        };
        const productImageFile = ref(null);
        const onProductImageChange = (e) => {
            productImageFile.value = e.target.files[0] || null;
        };
        const deleteProduct = (id) => {
            confirmDelete.value = { visible: true, message: '确定删除该商品吗？', callback: async () => {
                confirmDelete.value = { visible: false };
                const r = await del('/products/' + id);
                if (r.code === 200) { showToast('已删除'); loadProducts(); loadDashboard(); }
                else showToast(r.message, 'error');
            }};
        };
        const searchProducts = async () => {
            const r = await get('/products/search?category=' + (productSearch.category || '') + '&keyword=' + (productSearch.keyword || ''));
            if (r.code === 200) products.value = r.data;
        };

        // ========== 入库操作 ==========
        const openInboundForm = () => {
            Object.assign(inboundForm, { productId: null, batchNo: '', quantity: 1, operator: currentUser.realName || '管理员', remark: '', locationId: null, productionDate: '', supplier: '' });
            availableLocations.value = [];
            modalTitle.value = '创建入库单';
            modalType.value = 'inbound';
            showModal.value = true;
        };
        const onInboundProductChange = async () => {
            availableLocations.value = [];
            inboundForm.locationId = null;
            if (!inboundForm.productId) return;
            const p = products.value.find(x => x.id === inboundForm.productId);
            if (!p || !p.storageCondition) return;
            const r = await get('/locations/available?storageCondition=' + encodeURIComponent(p.storageCondition));
            if (r.code === 200) availableLocations.value = r.data;
        };
        const saveInbound = async () => {
            const r = await post('/inbound', inboundForm);
            if (r.code === 200) { showToast('入库单已创建'); showModal.value = false; loadInboundOrders(); loadDashboard(); }
            else showToast(r.message, 'error');
        };
        const qualityCheck = async (id, passed) => {
            try {
                const op = encodeURIComponent(currentUser.realName || '管理员');
                const r = await put('/inbound/' + id + '/quality?passed=' + passed + '&operator=' + op);
                if (r.code === 200) { showToast(passed ? '质检合格' : '质检不合格，已取消'); loadInboundOrders(); }
                else showToast(r.message || '操作失败', 'error');
            } catch (e) { showToast('请求失败: ' + e.message, 'error'); }
        };
        const putaway = async (id) => {
            try {
                const op = encodeURIComponent(currentUser.realName || '管理员');
                const r = await put('/inbound/' + id + '/putaway?operator=' + op);
                if (r.code === 200) { showToast('上架成功'); loadInboundOrders(); loadInventories(); loadDashboard(); }
                else showToast(r.message || '操作失败', 'error');
            } catch (e) { showToast('请求失败: ' + e.message, 'error'); }
        };
        const undoInbound = async () => {
            const r = await post('/inbound/undo');
            if (r.code === 200) { showToast('撤销成功'); loadInboundOrders(); loadInventories(); }
            else showToast(r.message, 'error');
        };

        // ========== 出库操作 ==========
        const openOutboundForm = () => {
            Object.assign(outboundForm, { productId: null, quantity: 1, operator: currentUser.realName || '管理员', pickingStrategy: pickingStrategy.value, customerOrderId: null, matchedBatch: '' });
            modalTitle.value = '创建出库单';
            modalType.value = 'outbound';
            showModal.value = true;
        };
        const onOutboundProductChange = async () => {
            if (!outboundForm.productId) return;
            const r = await get('/inventory/by-product/' + outboundForm.productId);
            if (r.code === 200 && r.data.length > 0) {
                const total = r.data.reduce((s, i) => s + (i.quantity || 0), 0);
                outboundForm.matchedBatch = '库存总量: ' + total + '，批次将由拣货策略自动匹配';
            } else {
                outboundForm.matchedBatch = '该商品暂无库存';
            }
        };
        const saveOutbound = async () => {
            const r = await post('/outbound', outboundForm);
            if (r.code === 200) { showToast('出库单已创建'); showModal.value = false; loadOutboundOrders(); }
            else showToast(r.message, 'error');
        };
        const confirmPicking = async (id) => {
            try {
                const op = encodeURIComponent(currentUser.realName || '管理员');
                const r = await put('/outbound/' + id + '/confirm?operator=' + op);
                if (r.code === 200) { showToast('拣货完成，库存已扣减'); loadOutboundOrders(); loadInventories(); loadDashboard(); }
                else showToast(r.message || '操作失败', 'error');
            } catch (e) { showToast('请求失败: ' + e.message, 'error'); }
        };
        const cancelOutbound = async (id) => {
            try {
                const op = encodeURIComponent(currentUser.realName || '管理员');
                const r = await put('/outbound/' + id + '/cancel?operator=' + op);
                if (r.code === 200) { showToast('已取消'); loadOutboundOrders(); }
                else showToast(r.message || '操作失败', 'error');
            } catch (e) {
                showToast('请求失败: ' + e.message, 'error');
            }
        };
        const undoOutbound = async () => {
            const r = await post('/outbound/undo');
            if (r.code === 200) { showToast('撤销成功'); loadOutboundOrders(); loadInventories(); }
            else showToast(r.message, 'error');
        };

        // ========== 订单操作 ==========
        const openOrderForm = () => {
            Object.assign(orderForm, { productId: null, quantity: 1, customerName: '', customerPhone: '', deliveryAddress: '' });
            modalTitle.value = '创建客户订单';
            modalType.value = 'order';
            showModal.value = true;
        };
        const saveOrder = async () => {
            const r = await post('/orders', orderForm);
            if (r.code === 200) { showToast('订单已创建'); showModal.value = false; loadCustomerOrders(); loadDashboard(); }
            else showToast(r.message, 'error');
        };
        const processOrder = async (id) => {
            const r = await post('/orders/' + id + '/process');
            if (r.code === 200) { showToast('订单已进入处理流水线（责任链模式）'); loadCustomerOrders(); loadOutboundOrders(); loadPickingTasks(); }
            else showToast(r.message, 'error');
        };
        const cancelOrder = async (id) => {
            try {
                const r = await put('/orders/' + id + '/cancel');
                if (r.code === 200) { showToast('订单已取消'); loadCustomerOrders(); loadDashboard(); }
                else showToast(r.message || '操作失败', 'error');
            } catch (e) { showToast('请求失败: ' + e.message, 'error'); }
        };

        // ========== 补货操作 ==========
        const openReplenishForm = () => {
            Object.assign(replenishForm, { productId: null, replenishQuantity: 1, reason: '低库存', creator: currentUser.realName || '管理员' });
            modalTitle.value = '创建补货任务';
            modalType.value = 'replenish';
            showModal.value = true;
        };
        const saveReplenish = async () => {
            const r = await post('/replenish', replenishForm);
            if (r.code === 200) { showToast('补货任务已创建'); showModal.value = false; loadReplenishTasks(); loadDashboard(); }
            else showToast(r.message, 'error');
        };
        const confirmReplenish = async (id) => {
            try {
                const r = await put('/replenish/' + id + '/confirm');
                if (r.code === 200) { showToast('补货完成'); loadReplenishTasks(); loadInventories(); loadDashboard(); }
                else showToast(r.message || '操作失败', 'error');
            } catch (e) { showToast('请求失败: ' + e.message, 'error'); }
        };

        // ========== 策略切换 ==========
        const switchStrategy = async (type) => {
            const r = await put('/inventory/strategy?type=' + type);
            if (r.code === 200) { pickingStrategy.value = type; showToast('已切换拣货策略: ' + type); loadInventories(); }
            else showToast(r.message, 'error');
        };

        // ========== 拣货任务操作 ==========
        const loadAvailablePickers = async () => {
            const r = await get('/picking/available-pickers');
            if (r.code === 200) availablePickers.value = r.data;
        };
        const openPickerSelect = async (taskId) => {
            pickerSelectTaskId.value = taskId;
            selectedPickerName.value = '';
            await loadAvailablePickers();
            modalTitle.value = '分配拣货员';
            modalType.value = 'picker-select';
            showModal.value = true;
        };
        const savePickerAssign = async () => {
            if (!selectedPickerName.value) { showToast('请选择拣货员', 'error'); return; }
            const r = await put('/picking/' + pickerSelectTaskId.value + '/assign?picker=' + encodeURIComponent(selectedPickerName.value));
            if (r.code === 200) { showToast('已分配拣货员: ' + selectedPickerName.value); showModal.value = false; loadPickingTasks(); loadDevices(); }
            else showToast(r.message || '操作失败', 'error');
        };
        const confirmPickingTask = async (taskId) => {
            try {
                const op = encodeURIComponent(currentUser.realName || '管理员');
                const r = await put('/outbound/' + taskId + '/confirm?operator=' + op);
                if (r.code === 200) { showToast('拣货确认成功，库存已扣减'); loadPickingTasks(); loadOutboundOrders(); loadInventories(); loadDashboard(); }
                else showToast(r.message || '操作失败', 'error');
            } catch (e) { showToast('请求失败: ' + e.message, 'error'); }
        };
        const moveToPickingArea = async (outboundId) => {
            try {
                const op = encodeURIComponent(currentUser.realName || '管理员');
                const r = await put('/outbound/' + outboundId + '/move?operator=' + op);
                if (r.code === 200) { showToast('已移入拣货区，货物已从货架移至拣货区'); loadOutboundOrders(); loadPickingTasks(); loadDashboard(); }
                else showToast(r.message || '操作失败', 'error');
            } catch (e) { showToast('请求失败: ' + e.message, 'error'); }
        };

        // ========== 系统配置更新（单例模式） ==========
        const updateConfig = async (key, value) => {
            try {
                const r = await put('/system/config?key=' + encodeURIComponent(key) + '&value=' + encodeURIComponent(value));
                if (r.code === 200) { showToast('配置已更新'); loadConfigs(); }
                else showToast(r.message || '操作失败', 'error');
            } catch (e) { showToast('请求失败: ' + e.message, 'error'); }
        };

        // ========== 登录 ==========
        const showLogin = ref(!getToken()); // 已有token则跳过登录
        globalShowLogin = showLogin;
        const loginForm = reactive({ username: 'admin', password: '123456' });
        const doLogin = async () => {
            const r = await post('/users/login', loginForm);
            if (r.code === 200) {
                if (r.data.token) setToken(r.data.token);
                Object.assign(currentUser, r.data);
                showLogin.value = false;
                showToast('欢迎, ' + r.data.realName);
                refreshAll();
                loadPickingStrategy();
            } else {
                showToast(r.message, 'error');
            }
        };
        const doLogout = () => {
            clearToken();
            showLogin.value = true;
            showToast('已退出登录');
        };

        // ========== 权限检查（代理模式） ==========
        const permCheckResult = ref('');
        const permCheckOp = ref('入库');
        const checkPerm = async () => {
            const r = await get('/system/check-permission?operation=' + encodeURIComponent(permCheckOp.value) + '&role=' + encodeURIComponent(currentUser.role));
            if (r.code === 200) permCheckResult.value = r.data.result;
        };

        // ========== 用户操作 ==========
        const openUserForm = (u = null) => {
            if (u) {
                Object.assign(userForm, { id: u.id, username: u.username, password: '', realName: u.realName, role: u.role, phone: u.phone || '', status: u.status });
                modalTitle.value = '编辑用户';
            } else {
                Object.assign(userForm, { id: null, username: '', password: '', realName: '', role: '仓管员', phone: '', status: 1 });
                modalTitle.value = '新增用户';
            }
            modalType.value = 'user';
            showModal.value = true;
        };
        const saveUser = async () => {
            const data = { ...userForm };
            if (data.id && !data.password) delete data.password; // 编辑时不改密码
            const r = data.id
                ? await put('/users/' + data.id, data)
                : await post('/users', data);
            if (r.code === 200) { showToast('保存成功'); showModal.value = false; loadUsers(); }
            else showToast(r.message, 'error');
        };
        const deleteUser = (id) => {
            confirmDelete.value = { visible: true, message: '确定删除该用户吗？', callback: async () => {
                confirmDelete.value = { visible: false };
                const r = await del('/users/' + id);
                if (r.code === 200) { showToast('已删除'); loadUsers(); }
                else showToast(r.message, 'error');
            }};
        };

        // ========== 工具函数 ==========
        const statusTag = (status) => {
            const m = {
                '正常': 'tag-success', '待质检': 'tag-warning', '质检中': 'tag-info', '已上架': 'tag-success',
                '已取消': 'tag-default', '合格': 'tag-success', '不合格': 'tag-danger',
                '待拣货': 'tag-warning', '拣货中': 'tag-info', '已拣货': 'tag-info', '已完成': 'tag-success',
                '待处理': 'tag-warning', '已锁定库存': 'tag-info', '已发货': 'tag-success',
                '待分配': 'tag-warning', '已分配': 'tag-info', '补货中': 'tag-info',
                '空闲': 'tag-success', '过期': 'tag-danger', '临期': 'tag-warning',
                '故障': 'tag-danger', '充电中': 'tag-warning', '作业中': 'tag-info',
            };
            return m[status] || 'tag-default';
        };

        const getProductName = (products, id) => {
            const p = products.find(x => x.id === id);
            return p ? p.name : '-';
        };
        const getProductUnit = (id) => {
            const p = products.value.find(x => x.id === id);
            return p ? p.unit : '';
        };

        return {
            activeTab, tabs, visibleTabs, switchTab, toastMsg, toastType,
            isAdmin, hasRole,
            currentUser, showLogin, loginForm, doLogin, doLogout,
            dashboard, products, inventories, inboundOrders, outboundOrders,
            customerOrders, pickingTasks, deviceStates, replenishTasks, operationLogs, reports, systemLogs, configs,
            expiringItems, lowStockItems, inventoryView,
            users, pickingStrategy, availableStrategies, switchStrategy,
            showModal, modalTitle, modalType,
            productForm, openProductForm, saveProduct, deleteProduct, productSearch, searchProducts,
            confirmDelete, productImageFile, onProductImageChange,
            inboundForm, openInboundForm, onInboundProductChange, saveInbound, qualityCheck, putaway, undoInbound,
            availableLocations,
            outboundForm, openOutboundForm, saveOutbound, moveToPickingArea, cancelOutbound, onOutboundProductChange,
            orderForm, openOrderForm, saveOrder, processOrder, cancelOrder,
            replenishForm, openReplenishForm, saveReplenish, confirmReplenish,
            userForm, openUserForm, saveUser, deleteUser,
            openPickerSelect, savePickerAssign, confirmPickingTask,
            availablePickers, pickerSelectTaskId, selectedPickerName,
            permissionOps, permCheckResult, permCheckOp, checkPerm,
            updateConfig,
            statusTag, getProductName, getProductUnit, showToast,
            loadProducts, loadInventories, loadInboundOrders, loadOutboundOrders,
            loadCustomerOrders, loadReplenishTasks, loadDashboard, loadOperationLogs,
            barRef, pieRef, loadChartData, drawCharts,
            loadPickingTasks, loadDevices, loadExpiring, loadLowStock,
            loadSystemLogs, loadConfigs, loadUsers, refreshAll,
            loadReports, reportPage, logPage, prevReportPage, nextReportPage, prevLogPage, nextLogPage,
            outboundPage, pickingPage, prevOutboundPage, nextOutboundPage, prevPickingPage, nextPickingPage,
        };
    }
});

app.mount('#app');
