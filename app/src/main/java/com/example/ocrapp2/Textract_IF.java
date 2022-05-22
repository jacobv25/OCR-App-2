package com.example.ocrapp2;

import com.amazonaws.services.textract.model.AnalyzeDocumentResult;

public interface Textract_IF {
    AnalyzeDocumentResult extractText();
}
