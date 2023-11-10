package com.report.system.report.service.domain.service;

import com.report.system.report.service.domain.valueObject.LogReport;
import java.util.List;

public interface ReportDomainService {

    LogReport processLogEntries(List<String> logLines);

}
