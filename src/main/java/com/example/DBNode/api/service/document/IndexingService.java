package com.example.DBNode.api.service.document;

import com.example.DBNode.api.model.DocumentsCollection;
import com.example.DBNode.utils.JsonToPropertyValueConverter;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IndexingService {

    public List<String> searchInIndex(DocumentsCollection collection, String jsonObject) throws JsonProcessingException {
        List<String> valuesToSearch = getValuesToSearch(jsonObject);
        if(isQueryEmpty(valuesToSearch))
            return Collections.emptyList();
        List<String> documentIds = getDocumentsIDs(collection,valuesToSearch);
        if(isResultEmpty(documentIds))
            return Collections.emptyList();
        return documentIds;
    }

    private List<String> getValuesToSearch(String jsonObject) throws JsonProcessingException {
        if(jsonObject == null )
            throw new IllegalArgumentException();
        return JsonToPropertyValueConverter.convert(jsonObject);
    }

    private boolean isQueryEmpty(List<String> valuesToSearch){
        if(valuesToSearch == null)
            throw new IllegalArgumentException();
        return valuesToSearch.size() == 0;
    }

    private List<String> getDocumentsIDs(DocumentsCollection collection, List<String> valuesToSearch){
        if(valuesToSearch == null || collection == null)
            throw new IllegalArgumentException();
        return collection.getIndex().findByMultiValues(valuesToSearch);
    }

    private boolean isResultEmpty(List<String> documentsIds){
        if(documentsIds == null)
            throw new IllegalArgumentException();
        return documentsIds.size() == 0;
    }
}
