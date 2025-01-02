package online.happlay.jingsai.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserStatusEnum {
    DISABLE(1, "禁用"),
    ENABLE(0, "启用");

    private Integer status;
    private String desc;


    public static UserStatusEnum getByStatus(Integer status) {
        for (UserStatusEnum item : UserStatusEnum.values()) {
            if (item.getStatus().equals(status)) {
                return item;
            }
        }
        return null;
    }
}
