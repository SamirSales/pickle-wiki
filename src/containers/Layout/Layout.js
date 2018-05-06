import React, { Component } from 'react';

import Aux from '../../hoc/Aux';
import NavBar from '../NavBar/NavBar';
import TopBar from '../TopBar/TopBar';
import Modal from '../../components/UI/Modal/Modal';

import './Layout.css';

export const AuthContext = React.createContext(false);

class Layout extends Component {

    state = {
        user: {
            name: 'Francisco',
            login: 'chico',
            password: '123456'
        },
        dialog:{
            title: '',
            message: '',
            active: false,
            funConfirm: null
        }
    }
    
    dialogLogout = () => {
        this.setState( {
            dialog: {
                title: 'Fazer logout',
                message: 'Tem certeza que deseja fazer logout?',
                active: true, 
                funConfirm: this.logout
            }
        });
    }
    
    closeDialog = () => {
        this.setState( {
            dialog: {
                active: false
            }
        });
    }

    logout = () => {
        this.setState( {
            dialog: {
                active: false
            },
            user: null
        });
    }

    render() {
        return (
            <Aux>
                <NavBar home={this.props.url}
                    title={this.props.appName}
                    itemClick={this.props.navItemClick} />

                <div id='main-content'>
                    <AuthContext.Provider value={this.state.user !== null}>
                        <TopBar home={this.state.url} 
                            user={this.state.user}
                            logout={this.dialogLogout} />
                    </AuthContext.Provider>

                    <main id='container'>{this.props.children}</main>
                </div>
   
                <Modal 
                    title={this.state.dialog.title}
                    message={this.state.dialog.message}
                    active={this.state.dialog.active}
                    confirm={this.state.dialog.funConfirm} 
                    cancel={this.closeDialog} />
            </Aux>
        );
    }  
}

export default Layout;