package k.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import k.model.Perfil;
import k.model.StatusPedido;

@Converter(autoApply = true)
public class StatusPedidoConverter implements AttributeConverter<StatusPedido, Integer>{

    @Override
    public Integer convertToDatabaseColumn(StatusPedido statusPedido) {
        return statusPedido == null ? null : statusPedido.getId();
    }

    @Override
    public StatusPedido convertToEntityAttribute(Integer id) {
        return StatusPedido.valueOf(id);
    }
    
}
