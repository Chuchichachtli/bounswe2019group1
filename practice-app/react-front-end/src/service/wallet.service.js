import { environment } from "environments/environment.prod";
import { authHeader } from "utils/auth-header";
import axios from "axios";

export function getMyWallet() {
  const requestOptions = {
    headers: {
      Authorization: authHeader()
    }
  };
  return axios(
    `${environment.api_url}wallet/retrieve/`,
    requestOptions
  ).then(res => (res.status === 200 ? res.data : null));
}


//http://35.163.120.227:8000/wallet/retrieve/
export function getMyWallet(){
  const requestOptions = {
    headers: {
      Authorization: authHeader()
    }
  };
  return axios(
    `${environment.api_url}wallet/retrieve/`,
    requestOptions
  ).then(res => (res.status === 200 ? res.data : null));
}
export function addFund(value){

  const requestOptions = {
    headers: {
      Authorization: authHeader(),
      "Content-Type": "application/json"
    },
    body:{
      "USD": value
    }
  };
  return axios.post(
      `${environment.api_url}wallet/sendUSD/`,
      requestOptions.body,
      {
        headers: requestOptions.headers
      }
  );
}

export function buyEquipment(values){

  const requestOptions = {
    headers: {
      Authorization: authHeader(),
      "Content-Type": "application/json"
    },
    body:{
      "name_of_eq": values.name,
      "amount": values.amount
    }
  };
  return axios.post(
      `${environment.api_url}wallet/takeequipment/`,
      requestOptions.body,
      {
        headers: requestOptions.headers
      }
  );
}

export function sellEquipment(values){
  const requestOptions = {
    headers: {
      Authorization: authHeader(),
      "Content-Type": "application/json"
    },
    body:{
      "name_of_eq": values.name,
      "amount": values.amount
    }
  };
  return axios.post(
      `${environment.api_url}wallet/sellequipment/`,
      requestOptions.body,
      {
        headers: requestOptions.headers
      }
  );
}


export function getWalletById2(user_id) {
  const requestOptions = {
    headers: {
      Authorization: authHeader()
    }
  };
  return axios(
    `${environment.api_url}wallet/retrieve/${user_id}/`,
    requestOptions
  ).then(res => (res.status === 200 ? res.data : null));
}
