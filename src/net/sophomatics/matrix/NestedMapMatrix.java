package net.sophomatics.matrix;

import java.util.*;

/**
 * Created by mark on 12.07.15.
 */
public class NestedMapMatrix<A, B, C> extends HashMap<A, Map<B, C>> implements Matrix<A, B, C> {
    private final int maxSize;

    public NestedMapMatrix(int maxSize) {
        this.maxSize = maxSize;
    }

    public NestedMapMatrix() {
        this(0);
    }

    private String crop(String str, int length) {
        String newString = str;
        int strLength = newString.length();
        if (length < strLength) {
            newString = newString.substring(0, length);
        } else if (strLength < length) {
            for (int i = 0; i < length - strLength; i++) {
                newString = " " + newString;
            }
        }
        return newString;
    }

    @Override
    public String print() {
        final int cellSize = 4;
        // determine all keys
        List<A> rowHeader = new ArrayList<>(this.keySet());
        List<B> columnHeader = new ArrayList<>();
        for (Map<B, C> row : this.values()) {
            for (B key : row.keySet()) {
                if (!columnHeader.contains(key)) {
                    columnHeader.add(key);
                }
            }
        }

        // return empty matrix
        if (rowHeader.size() < 1 || columnHeader.size() < 1) {
            return "<empty matrix>\n";
        }

        // sort if sortable
        if (rowHeader.get(0) instanceof Comparable) {
            Collections.sort((List) rowHeader);
        }
        if (columnHeader.get(0) instanceof Comparable) {
            Collections.sort((List) columnHeader);
        }

        // determine row format
        String rowFormat = "";
        for (int eachColumn = 0; eachColumn < columnHeader.size(); eachColumn++) {
            rowFormat += "%s\t";
        }
        rowFormat += "%s\n";

        // determine longest row key
        List<String> eachRow = new ArrayList<>();
        int thisLen, maxLen = -1;
        for (A eachRowKey : rowHeader) {
            eachRow.clear();
            thisLen = eachRowKey.toString().length();
            if (maxLen < thisLen) {
                maxLen = thisLen;
            }
        }
        int rowHdrSize = Math.min(maxLen, 32);

        // set string elements of first line
        eachRow.add(this.crop("-", rowHdrSize));
        for (B eachColumnHeader : columnHeader) {
            eachRow.add(this.crop(eachColumnHeader.toString(), cellSize));
        }

        // write first line
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(rowFormat, eachRow.toArray()));

        // write remaining lines
        Map<B, C> row;
        C value;
        for (A eachRowKey : rowHeader) {
            eachRow.clear();
            eachRow.add(this.crop(eachRowKey.toString(), rowHdrSize));
            row = this.get(eachRowKey);
            for (B eachColumnKey : columnHeader) {
                value = row.get(eachColumnKey);
                if (value == null) {
                    eachRow.add(this.crop("null", cellSize));
                } else {
                    eachRow.add(this.crop(value.toString(), cellSize));
                }
            }
            sb.append(String.format(rowFormat, eachRow.toArray()));
        }
        return sb.toString();
    }

    @Override
    public Set<B> getKeys(A k) {
        Map<B, C> row = this.get(k);
        if (row == null) {
            return new HashSet<>();
        } else {
            return new HashSet<>(row.keySet());
        }
    }

    @Override
    public List<C> getValues(A k) {
        Map<B, C> row = this.get(k);
        if (row == null) {
            return new ArrayList<>();
        } else {
            return new ArrayList<>(row.values());
        }
    }

    @Override
    public Map<B, C> getRow(A key) {
        Map<B, C> row = this.get(key);
        if (row == null) {
            if (this.maxSize < 1) {
                row = new HashMap<>();

            } else {
                row = new HashMap<>(this.maxSize);
            }
            this.put(key, row);
        }

        return row;
      }

    @Override
    public C put(A k0, B k1, C v) {
        Map<B, C> row = this.getRow(k0);
        return row.put(k1, v);
    }

    @Override
    public C get(A k0, B k1) {
        Map<B, C> row = this.get(k0);
        if (row == null) {
            return null;
        }
        return row.get(k1);
    }


    @Override
    public void integrate(Matrix<A, B, C> other) {
        NestedMapMatrix<A, B, C> cast = (NestedMapMatrix<A, B, C>) other;

        A eachKey;
        Map<B, C> thisRow, otherRow;

        for (Map.Entry<A, Map<B, C>> entry : cast.entrySet()) {
            eachKey = entry.getKey();
            otherRow = entry.getValue();
            thisRow = this.getRow(eachKey);

            for (Map.Entry<B, C> subEntry : otherRow.entrySet()) {
                thisRow.put(subEntry.getKey(), subEntry.getValue());
            }
        }

    }

}
