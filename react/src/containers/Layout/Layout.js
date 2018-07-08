import React, { Component } from 'react';

import { connect } from 'react-redux';
import Aux from '../../hoc/Aux/Aux';
import NavBar from '../NavBar/NavBar';
import TopBar from '../TopBar/TopBar';
import ConfirmModal from '../../components/UI/Modal/ConfirmModal/ConfirmModal';
import LoginModal from '../../components/UI/Modal/LoginModal/LoginModal';
import Spinner from '../../components/UI/Spinner/Spinner';
import * as actionTypes from '../../store/actions';

import './Layout.css';

export const AuthContext = React.createContext(false);

export const showSnackBar = (text) => {
    var x = document.getElementById("snackbar");
    x.innerHTML = text;
    x.className = "show";

    // After 3 seconds, remove the show class from DIV
    setTimeout(function(){ x.className = x.className.replace("show", ""); }, 3000);
}

class Layout extends Component {

    state = {
        dialogLogout:{
            active: false,
            funConfirm: null
        },
        dialogLogin:{
            active: false
        },
        loading: false
    }
    
    /* Log in Methods */

    dialogLogin = () => {
        this.setState( {
            dialogLogin: {
                active: true
            }
        });
    }

    closeDialogLogin = () => {
        this.setState( {
            dialogLogin: {
                active: false
            }
        });

        // clean the fields
        document.getElementById("inputLoginModal").value = "";
        document.getElementById("inputPasswordModal").value = "";
    }

    login = (login, password) => {
        if(login === 'admin' && password === 'admin'){
        
            this.props.onLogin({
                name: 'Francisco',
                login: 'chico',
                password: '123456',
                type: 'ADMIN'
            });

            this.closeDialogLogin();
            showSnackBar('Bem vindo!!!');
            return true;
        }
        
        showSnackBar('Acesso Negado');
        return false;
    }

    /* Log out methods */

    dialogLogout = () => {
        this.setState( {
            dialogLogout: {
                active: true, 
                funConfirm: this.logout
            }
        });
    }
    
    closeDialogLogout = () => {
        this.setState( {
            dialogLogout: {
                active: false
            }
        });
    }

    logout = () => {
        this.setState( {
            dialogLogout: {
                active: false
            }
        });

        this.props.onLogin(null);
    }

    render() {
        return (
            <Aux>
                <NavBar home={this.props.url}
                    title={this.props.appName}
                    itemClick={this.props.navItemClick} />

                <div id='main-content'>
                    <AuthContext.Provider value={this.props.usr !== null}>
                        <TopBar home={this.state.url} 
                            user={this.props.usr}
                            logout={this.dialogLogout}
                            login={this.dialogLogin}
                            {...this.props} />
                    </AuthContext.Provider>

                    <main id='container'>{this.props.children}</main>
                </div>
   
                <Spinner
                    marginTop='13%'
                    marginLeft='calc(50% - 221px)'
                    active={this.state.loading} />

                <ConfirmModal 
                    title="Fazer logout"
                    question="Tem certeza que deseja fazer logout?"
                    active={this.state.dialogLogout.active}
                    marginTop='15%'
                    marginLeft='calc(50% - 221px)'
                    confirm={this.state.dialogLogout.funConfirm} 
                    cancel={this.closeDialogLogout} />

                <LoginModal 
                    active={this.state.dialogLogin.active}
                    marginTop='15%'
                    marginLeft='calc(50% - 221px)'
                    confirm={this.login} 
                    cancel={this.closeDialogLogin} />

                <div id="snackbar"></div>
            </Aux>
        );
    }  
}

const mapStateToProps = state => {
    return{
        usr: state.usr.user,
        appName: state.app.appName
    };
}

const mapDispathToProps = dispatch => {
    return{
        onLogin: (usr) => dispatch({type: actionTypes.USER_LOGIN, user: usr}),
        getAppName: () => dispatch({type: actionTypes.APP_NAME})
    };
}

export default connect(mapStateToProps, mapDispathToProps, null, {pure:false})(Layout);