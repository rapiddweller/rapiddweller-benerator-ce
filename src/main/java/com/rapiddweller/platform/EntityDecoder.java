package com.rapiddweller.platform;

import com.rapiddweller.model.data.ComplexTypeDescriptor;
import com.rapiddweller.model.data.Entity;

import java.util.List;

public interface EntityDecoder {
  Entity decodeEntity(String code);
  List<Entity> decodeList(String code, ComplexTypeDescriptor descriptor);
}
