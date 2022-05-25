$(function () {
    new Vue({
        el: "#app",
        data() {
            return {
                // 默认显示欢迎页
                index: '1',
                // 欢迎页日历
                value: new Date(),
                // 用户头像
                url: 'https://cn.vuejs.org/images/logo.svg',
                // 会议申请 人员表格
                tableData: [],
                // 会议申请 分页工具条
                pagination: {//分页相关模型数据
                    currentPage: 1,//当前页码
                    pageSize: 10,//每页显示的记录数
                    total: 0,//总记录数
                    name: '',//姓名
                    dept: '',//部门
                    post: '',//职位
                    gender: ''//性别
                },
                // 人员确认后临时存储
                multipleSelection: [],
                // 会议申请 表单/表单校验
                ruleForm: {
                    meetingName: '',//会议名称
                    meetingPlace: '',//会议地点
                    meetingTime: '',//会议日期
                    meetingDesc: '',//会议内容
                    persons: '',//选中的参会的人员
                    status: ''//默认状态为未开始
                },
                rules: {
                    meetingName: [
                        {required: true, message: '请输入会议名称', trigger: 'blur'},
                        {min: 3, max: 50, message: '长度在 3 到 50 个字符', trigger: 'blur'}
                    ],
                    meetingPlace: [
                        {required: true, message: '请选择会议室', trigger: 'change'}
                    ],
                    meetingTime: [
                        {type: 'date', required: true, message: '请选择日期', trigger: 'change'}
                    ],
                    meetingDesc: [
                        {required: true, message: '请填写会议内容', trigger: 'blur'}
                    ],
                    persons: [
                        {required: true, message: '请选择参会人员', trigger: 'input'}
                    ]
                },
                //选择会议人员对话框
                personsDialogVisible: false,
                // 会议列表
                meetingList: [],
                // 会议列表 编辑
                editDialogVisible: false,
                admin: ''
            };
        },
        methods: {
            // 导航二级菜单
            handleOpen(key, keyPath) {
                // console.log(key, keyPath);
            },
            handleClose(key, keyPath) {
                // console.log(key, keyPath);
            },
            // 导航内容切换
            setIndex(val) {
                this.index = val;
            },
            // 会议申请 人员列表
            selectPersons() {
                this.getAll();
                this.personsDialogVisible = true;
            },
            // 分页查询数据库人员并回填列表
            getAll() {
                axios.get("/meeting/" + this.pagination.currentPage + "/" + this.pagination.pageSize).then((res) => {
                    this.tableData = res.data.data.records;
                    this.pagination.currentPage = res.data.data.current;
                    this.pagination.pageSize = res.data.data.size;
                    this.pagination.total = res.data.data.total;
                })
            },
            // 变色表格
            tableRowClassName({row, rowIndex}) {
                if (rowIndex % 3 == 1) {
                    return 'warning-row';
                } else if (rowIndex % 3 != 2) {
                    return 'success-row';
                }
                return '';
            },
            // 人员列表选择人员
            handleSelectionChange(val) {
                this.multipleSelection = val;
            },
            // 人员列表选中确认后遍历获取所有id，并赋值给表单等待提交
            selectPerson() {
                var str = [];
                var val = this.multipleSelection;
                for (let i = 0; i < val.length; i++) {
                    str.push(val[i].id);
                }
                this.ruleForm.persons = str.toString();
                this.personsDialogVisible = false;
            },
            // 分页
            handleSizeChange(val) {
                this.pagination.pageSize = val;
                this.getAll();
            }
            ,
            handleCurrentChange(val) {
                this.pagination.currentPage = val;
                this.getAll();
            }
            ,
            // 会议申请
            meetingApply() {
                this.setIndex(2);
                this.ruleForm = {}
            },
            // 会议申请表单
            submitForm(formName) {
                this.$refs[formName].validate((valid) => {
                    if (valid) {
                        axios.post("/meeting", this.ruleForm).then((res) => {
                            if (res.data.flag) {
                                this.resetForm('ruleForm');
                                this.$message.success(res.data.msg);
                            } else {
                                this.$message.error(res.data.msg);
                            }
                        })
                    } else {
                        this.$message.error('请校验表单后再提交 =_*');
                        return false;
                    }
                });
            },
            resetForm(formName) {
                this.$refs[formName].resetFields();
            },
            // 会议列表
            meetingManagerList() {
                this.index = 3;
                this.meetingTable();
            },
            // 会议列表 查询数据
            meetingTable() {
                axios.get("/meeting").then((res) => {
                    this.meetingList = res.data.data;
                })
            },
            // 会议列表 编辑
            handleEdit(row) {
                axios.get("/meeting/"+row.id).then((res)=>{
                    if (res.data.flag && res.data.data != null) {
                        this.editDialogVisible = true;
                        this.ruleForm = res.data.data;
                    } else {
                        this.$message.error("数据同步失败，自动刷新");
                    }
                }).finally(()=>{
                    this.meetingTable();
                })
            },
            // 会议列表 删除
            handleDelete(row) {
                this.$confirm("确定要删除吗？", "提示", {type: "info",}).then(() => {
                    axios.delete("/meeting/" + row.id).then((res) => {
                        if (res.data.flag) {
                            this.$message.success(res.data.msg);
                        } else {
                            this.$message.error(res.data.msg);
                        }
                    }).finally(() => {
                        this.meetingTable();
                    })
                }).catch(() => {
                    this.$message.info("取消操作")
                })
            },
            // 会议列表 修改
            updateForm() {
                axios.put("/meeting", this.ruleForm).then((res)=>{
                    if (res.data.flag) {
                        this.editDialogVisible = false;
                        this.$message.success("修改成功");
                        this.resetForm('ruleForm');
                    } else {
                        this.$message.error("修改失败");
                    }
                }).finally(()=>{
                    this.meetingTable();
                })
            }
        }
    });
})