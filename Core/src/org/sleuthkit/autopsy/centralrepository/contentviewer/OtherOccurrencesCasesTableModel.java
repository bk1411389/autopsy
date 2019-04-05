/*
 * Central Repository
 *
 * Copyright 2019 Basis Technology Corp.
 * Contact: carrier <at> sleuthkit <dot> org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sleuthkit.autopsy.centralrepository.contentviewer;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.openide.util.NbBundle.Messages;
import org.sleuthkit.autopsy.centralrepository.datamodel.CorrelationCase;

/**
 * Model for cells in the cases section of the other occurrences data content
 * viewer
 */
public class OtherOccurrencesCasesTableModel extends AbstractTableModel {

    private static final long serialVersionUID = 1L;
    private final List<CorrelationCaseWrapper> correlationCaseList = new ArrayList<>();

    /**
     * Create a table model for displaying case names
     */
    OtherOccurrencesCasesTableModel() {
    }

    @Override
    public int getColumnCount() {
        return 1;
    }

    @Override
    public int getRowCount() {
        return correlationCaseList.size();
    }

    @Messages({"OtherOccurrencesCasesTableModel.case=Case",})
    @Override
    public String getColumnName(int colIdx) {
        return Bundle.OtherOccurrencesCasesTableModel_case();
    }

    @Messages({"OtherOccurrencesCasesTableModel.noData=No Data."})
    @Override
    public Object getValueAt(int rowIdx, int colIdx) {
        if (0 == correlationCaseList.size()) {
            return Bundle.OtherOccurrencesCasesTableModel_noData();
        }

        String value = correlationCaseList.get(rowIdx).getMessage();
        if (value == null || value.isEmpty()) {
            value = Bundle.OtherOccurrencesCasesTableModel_noData();
        }
        return value;
    }

    /**
     * Get a correlation case for the selected index. Does not query the Central
     * Repository so CorrelationCase will be partial missing CR case ID and
     * other information that is stored in the CR.
     *
     * @param rowIdx the row from the table model which corresponds to the case
     *
     * @return CorrelationCase for the table item specified
     */
    CorrelationCase getCorrelationCase(int rowIdx) {
        if (rowIdx < correlationCaseList.size()) {
            return correlationCaseList.get(rowIdx).getCorrelationCase();
        } else {
            return null;
        }
    }

    @Override
    public Class<String> getColumnClass(int colIdx) {
        return String.class;
    }

    /**
     * Add one correlation case wrapper object to the table
     *
     * @param newCorrelationCaseWrapper data to add to the table
     */
    void addCorrelationCase(CorrelationCaseWrapper newCorrelationCaseWrapper) {
        correlationCaseList.add(newCorrelationCaseWrapper);
        fireTableDataChanged();
    }

    /**
     * Clear the correlation case table.
     */
    void clearTable() {
        correlationCaseList.clear();
        fireTableDataChanged();
    }
}
