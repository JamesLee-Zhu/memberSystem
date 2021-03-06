<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/commons/global.jsp" %>
<script type="text/javascript">
    $(function() {
        $('#roleEditForm').form({
            url : '${path }/role/edit',
            onSubmit : function() {
                progressLoad();
                var isValid = $(this).form('validate');
                if (!isValid) {
                    progressClose();
                }
                return isValid;
            },
            success : function(result) {
                progressClose();
                result = $.parseJSON(result);
                if (result.code == "000") {
                    parent.$.modalDialog.handler.dialog('close');
                    parent.$.modalDialog.openner_dataGrid.datagrid('reload');
                } else {
                    showMsg(result.msg);
                }
            }
        });
        $("#roleEditStatus").val('${role.status}');
    });
</script>
<div class="easyui-layout" data-options="fit:true,border:false">
    <div data-options="region:'center',border:false" title="" style="overflow: hidden;padding: 3px;">
        <form id="roleEditForm" method="post">
            <table class="grid">
                <tr>
                    <td>角色名称</td>
                    <td><input name="id" type="hidden"  value="${role.id}">
                    <input name="name" placeholder="请输入角色名称" class="easyui-textbox" data-options="required:true" value="${role.name}"></td>
                </tr>
                <tr>
                    <td>角色编码</td>
                    <td><input name="code" placeholder="请输入角色编码" class="easyui-textbox" data-options="required:true" value="${role.code}"></td>
                </tr>
                <tr>
                    <td>排序</td>
                    <td><input name="sort"  class="easyui-numberspinner" required="required" data-options="editable:false" value="${role.sort}"></td>
                </tr>
                <tr>
                    <td>状态</td>
                    <td >
                        <select id="roleEditStatus" name="status" class="easyui-combobox" data-options="width:150,height:29,editable:false,panelHeight:'auto'">
                            <option value="0">启用</option>
                            <option value="1">停用</option>
                        </select>
                    </td>
                </tr>
                <tr>
                    <td>备注</td>
                    <td>
                    	<input name="description" style="width:200px;height:50px" class="easyui-textbox" data-options="multiline:true" value="${role.description}"/>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>