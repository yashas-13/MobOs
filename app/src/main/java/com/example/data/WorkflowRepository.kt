package com.example.data

import kotlinx.coroutines.flow.Flow

class WorkflowRepository(private val workflowDao: WorkflowDao) {
    val allWorkflows: Flow<List<WorkflowEntity>> = workflowDao.getAllWorkflowsFlow()
    val allHistories: Flow<List<ExecutionHistoryEntity>> = workflowDao.getAllHistoriesFlow()

    suspend fun insertWorkflow(workflow: WorkflowEntity): Long {
        return workflowDao.insertWorkflow(workflow)
    }

    suspend fun deleteWorkflow(workflow: WorkflowEntity) {
        workflowDao.deleteWorkflow(workflow)
    }

    suspend fun getActiveWorkflows(): List<WorkflowEntity> {
        return workflowDao.getActiveWorkflows()
    }

    suspend fun insertHistory(history: ExecutionHistoryEntity): Long {
        return workflowDao.insertHistory(history)
    }

    suspend fun clearHistory() {
        workflowDao.clearHistory()
    }

    suspend fun clearWorkflows() {
        workflowDao.clearWorkflows()
    }
}
