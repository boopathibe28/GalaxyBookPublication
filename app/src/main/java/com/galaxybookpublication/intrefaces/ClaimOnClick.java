package com.galaxybookpublication.intrefaces;

public interface ClaimOnClick {
    void onClickEdit(String uuid,String claim_for_label,String claim_for,String amount,String proof,String status);
    void onClickDelete(String uuid);
}
