package com.segnities007.stylish_myvehicles.app.di

import com.segnities007.stylish_myvehicles.data.local.AppDatabase
import com.segnities007.stylish_myvehicles.data.repository.CostRecordRepositoryImpl
import com.segnities007.stylish_myvehicles.data.repository.FuelRecordRepositoryImpl
import com.segnities007.stylish_myvehicles.data.repository.MaintenanceRecordRepositoryImpl
import com.segnities007.stylish_myvehicles.data.repository.MaintenanceScheduleRepositoryImpl
import com.segnities007.stylish_myvehicles.data.repository.VehicleRepositoryImpl
import com.segnities007.stylish_myvehicles.domain.repository.CostRecordRepository
import com.segnities007.stylish_myvehicles.domain.repository.FuelRecordRepository
import com.segnities007.stylish_myvehicles.domain.repository.MaintenanceRecordRepository
import com.segnities007.stylish_myvehicles.domain.repository.MaintenanceScheduleRepository
import com.segnities007.stylish_myvehicles.domain.repository.VehicleRepository
import com.segnities007.stylish_myvehicles.domain.usecase.ExportDataUseCase
import com.segnities007.stylish_myvehicles.domain.usecase.cost.DeleteCostRecordUseCase
import com.segnities007.stylish_myvehicles.domain.usecase.cost.GetCostRecordsUseCase
import com.segnities007.stylish_myvehicles.domain.usecase.cost.InsertCostRecordUseCase
import com.segnities007.stylish_myvehicles.domain.usecase.cost.UpdateCostRecordUseCase
import com.segnities007.stylish_myvehicles.domain.usecase.fuel.DeleteFuelRecordUseCase
import com.segnities007.stylish_myvehicles.domain.usecase.fuel.GetFuelRecordsUseCase
import com.segnities007.stylish_myvehicles.domain.usecase.fuel.InsertFuelRecordUseCase
import com.segnities007.stylish_myvehicles.domain.usecase.fuel.UpdateFuelRecordUseCase
import com.segnities007.stylish_myvehicles.domain.usecase.maintenance.DeleteMaintenanceRecordUseCase
import com.segnities007.stylish_myvehicles.domain.usecase.maintenance.GetMaintenanceRecordsUseCase
import com.segnities007.stylish_myvehicles.domain.usecase.maintenance.GetMaintenanceSchedulesUseCase
import com.segnities007.stylish_myvehicles.domain.usecase.maintenance.InsertMaintenanceRecordUseCase
import com.segnities007.stylish_myvehicles.domain.usecase.maintenance.UpdateMaintenanceRecordUseCase
import com.segnities007.stylish_myvehicles.domain.usecase.maintenance.UpdateMaintenanceScheduleUseCase
import com.segnities007.stylish_myvehicles.domain.usecase.vehicle.DeleteVehicleUseCase
import com.segnities007.stylish_myvehicles.domain.usecase.vehicle.GetVehicleUseCase
import com.segnities007.stylish_myvehicles.domain.usecase.vehicle.GetVehiclesUseCase
import com.segnities007.stylish_myvehicles.domain.usecase.vehicle.InsertVehicleUseCase
import com.segnities007.stylish_myvehicles.domain.usecase.vehicle.UpdateVehicleUseCase
import com.segnities007.stylish_myvehicles.presentation.screen.cost.CostListViewModel
import com.segnities007.stylish_myvehicles.presentation.screen.fuel.FuelRecordViewModel
import com.segnities007.stylish_myvehicles.presentation.screen.maintenance.MaintenanceRecordViewModel
import com.segnities007.stylish_myvehicles.presentation.screen.notification.NotificationViewModel
import com.segnities007.stylish_myvehicles.presentation.screen.records.RecordsViewModel
import com.segnities007.stylish_myvehicles.presentation.screen.vehicledetail.VehicleDetailViewModel
import com.segnities007.stylish_myvehicles.presentation.screen.vehicleedit.VehicleEditViewModel
import com.segnities007.stylish_myvehicles.presentation.screen.vehiclelist.VehicleListViewModel
import com.segnities007.stylish_myvehicles.presentation.screen.vehiclepager.VehiclePagerViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // Database
    single { AppDatabase.create(androidContext()) }
    single { get<AppDatabase>().vehicleDao() }
    single { get<AppDatabase>().fuelRecordDao() }
    single { get<AppDatabase>().maintenanceRecordDao() }
    single { get<AppDatabase>().costRecordDao() }
    single { get<AppDatabase>().maintenanceScheduleDao() }

    // Repository
    single<VehicleRepository> { VehicleRepositoryImpl(get()) }
    single<FuelRecordRepository> { FuelRecordRepositoryImpl(get()) }
    single<MaintenanceRecordRepository> { MaintenanceRecordRepositoryImpl(get()) }
    single<MaintenanceScheduleRepository> { MaintenanceScheduleRepositoryImpl(get()) }
    single<CostRecordRepository> { CostRecordRepositoryImpl(get()) }

    // UseCase - Vehicle
    single { GetVehiclesUseCase(get()) }
    single { GetVehicleUseCase(get()) }
    single { InsertVehicleUseCase(get(), get()) }
    single { UpdateVehicleUseCase(get()) }
    single { DeleteVehicleUseCase(get()) }

    // UseCase - Fuel
    single { GetFuelRecordsUseCase(get()) }
    single { InsertFuelRecordUseCase(get(), get()) }
    single { UpdateFuelRecordUseCase(get()) }
    single { DeleteFuelRecordUseCase(get()) }

    // UseCase - Maintenance
    single { GetMaintenanceRecordsUseCase(get()) }
    single { GetMaintenanceSchedulesUseCase(get()) }
    single { InsertMaintenanceRecordUseCase(get(), get(), get()) }
    single { UpdateMaintenanceRecordUseCase(get()) }
    single { UpdateMaintenanceScheduleUseCase(get()) }
    single { DeleteMaintenanceRecordUseCase(get()) }

    // UseCase - Cost
    single { GetCostRecordsUseCase(get()) }
    single { InsertCostRecordUseCase(get()) }
    single { UpdateCostRecordUseCase(get()) }
    single { DeleteCostRecordUseCase(get()) }

    // UseCase - Export
    single { ExportDataUseCase() }

    // ViewModel
    viewModel { VehicleListViewModel(get()) }
    viewModel {
        VehiclePagerViewModel(
            getVehiclesUseCase = get(),
            getCostRecordsUseCase = get(),
            getFuelRecordsUseCase = get(),
            getMaintenanceSchedulesUseCase = get(),
        )
    }
    viewModel { params ->
        VehicleDetailViewModel(
            vehicleId = params.get(),
            getVehicleUseCase = get(),
            getFuelRecordsUseCase = get(),
            getMaintenanceRecordsUseCase = get(),
            getCostRecordsUseCase = get(),
            getMaintenanceSchedulesUseCase = get(),
            updateMaintenanceScheduleUseCase = get(),
            exportDataUseCase = get(),
        )
    }
    viewModel { params ->
        VehicleEditViewModel(
            vehicleId = params.getOrNull(),
            getVehicleUseCase = get(),
            insertVehicleUseCase = get(),
            updateVehicleUseCase = get(),
            deleteVehicleUseCase = get(),
        )
    }
    viewModel { params ->
        FuelRecordViewModel(
            vehicleId = params.get(),
            getFuelRecordsUseCase = get(),
            insertFuelRecordUseCase = get(),
            updateFuelRecordUseCase = get(),
            deleteFuelRecordUseCase = get(),
        )
    }
    viewModel { params ->
        MaintenanceRecordViewModel(
            vehicleId = params.get(),
            getMaintenanceRecordsUseCase = get(),
            insertMaintenanceRecordUseCase = get(),
            updateMaintenanceRecordUseCase = get(),
            deleteMaintenanceRecordUseCase = get(),
            getVehicleUseCase = get(),
            getMaintenanceSchedulesUseCase = get(),
        )
    }
    viewModel { params ->
        CostListViewModel(
            vehicleId = params.get(),
            getCostRecordsUseCase = get(),
            insertCostRecordUseCase = get(),
            updateCostRecordUseCase = get(),
            deleteCostRecordUseCase = get(),
        )
    }
    viewModel { params ->
        RecordsViewModel(
            vehicleId = params.get(),
            topic = params.get(),
            initialPeriodMode = params.get(),
            getFuelRecordsUseCase = get(),
            getMaintenanceRecordsUseCase = get(),
            getCostRecordsUseCase = get(),
            getVehiclesUseCase = get(),
        )
    }
    viewModel {
        NotificationViewModel(
            getVehiclesUseCase = get(),
            getMaintenanceSchedulesUseCase = get(),
        )
    }
}
