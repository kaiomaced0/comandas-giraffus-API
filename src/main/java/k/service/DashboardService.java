package k.service;

import java.time.LocalDate;

import k.dto.DashboardKpisDTO;

public interface DashboardService {

    DashboardKpisDTO kpis(LocalDate from, LocalDate to);

}
